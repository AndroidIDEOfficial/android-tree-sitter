/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension
import com.itsaky.androidide.treesitter.BuildTreeSitterTask
import com.itsaky.androidide.treesitter.CleanTreeSitterBuildTask
import com.itsaky.androidide.treesitter.projectVersionCode
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

buildscript {
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.25")
  }
}

@Suppress("DSL_SCOPE_VIOLATION") plugins {
  id("build-logic.root-project")
  alias(libs.plugins.kotlin) apply false
  alias(libs.plugins.maven.publish) apply false
}

fun Project.configureBaseExtension() {
  extensions.configure<BaseExtension> {
    compileSdkVersion(34)

    defaultConfig {
      minSdk = 21
      targetSdk = 33
      versionCode = project.projectVersionCode
      versionName = rootProject.version.toString()
    }

    compileOptions {
      sourceCompatibility = BuildConfig.JAVA_VERSION
      targetCompatibility = BuildConfig.JAVA_VERSION

      isCoreLibraryDesugaringEnabled = true
    }

    buildTypes {
      getByName("release") {
        isMinifyEnabled = false
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
          "proguard-rules.pro")
      }
    }

    configurations.getByName("coreLibraryDesugaring").dependencies.add(
      libs.common.coreLibDesugaring.get())
  }
}

subprojects {
  plugins.withId("com.android.application") { configureBaseExtension() }
  plugins.withId("com.android.library") { configureBaseExtension() }
  plugins.withId("java-library") {
    tasks.withType(JavaCompile::class.java) {
      sourceCompatibility = BuildConfig.JAVA_VERSION.majorVersion
      targetCompatibility = BuildConfig.JAVA_VERSION.majorVersion
    }
  }
  plugins.withId("android-tree-sitter.ts") {
    configureTsModule()

    // set java library path for tests
    tasks.withType<Test> {
      systemProperty("java.library.path",
        rootProject.buildDir.resolve("host").absolutePath)
    }
  }

  plugins.withId("com.vanniktech.maven.publish.base") {
    configure<MavenPublishBaseExtension> {
      group = "com.itsaky.androidide.treesitter"
      var versionName = rootProject.version.toString()
      if (!System.getenv("PublishToMaven").toBoolean()) {
        versionName = "$versionName-SNAPSHOT"
      }
      versionName = versionName.substring(1) // remove 'v' prefix

      pom {
        name.set(project.name)

        description.set(
          if (project.description.isNullOrBlank()) "${project.name} grammar for android-tree-sitter."
          else project.description)

        inceptionYear.set("2022")
        url.set("https://github.com/itsaky/android-tree-sitter/")

        licenses {
          license {
            name.set("LGPL-v2.1")
            url.set(
              "https://github.com/itsaky/android-tree-sitter/blob/main/LICENSE")
            distribution.set("repo")
          }
        }

        scm {
          url.set("https://github.com/itsaky/android-tree-sitter/")
          connection.set(
            "scm:git:git://github.com/itsaky/android-tree-sitter.git")
          developerConnection.set(
            "scm:git:ssh://git@github.com/itsaky/android-tree-sitter.git")
        }

        developers {
          developer {
            id.set("androidide")
            name.set("AndroidIDE")
            url.set("https://androidide.com")
          }
        }
      }

      coordinates(project.group.toString(), project.name, versionName)
      publishToMavenCentral(host = SonatypeHost.S01)
      signAllPublications()
      if (plugins.hasPlugin("java-library")) {
        configure(
          JavaLibrary(javadocJar = JavadocJar.Javadoc(), sourcesJar = true))
      } else {
        configure(AndroidSingleVariantLibrary(publishJavadocJar = false))
      }
    }
  }
}

tasks.register<BuildTreeSitterTask>("buildTreeSitter")

tasks.register<CleanTreeSitterBuildTask>("cleanTreeSitterBuild")

tasks.register<Delete>("clean").configure {
  dependsOn("cleanTreeSitterBuild")
  delete(rootProject.layout.buildDirectory)
  delete(rootProject.file("tree-sitter-lib/cli/build"))
}

fun Project.configureTsModule() {
  extensions.configure<BaseExtension> {
    val grammarName = project.project.name.substringAfter("tree-sitter-", "")
    if (grammarName.isNotBlank()) {
      namespace = "com.itsaky.androidide.treesitter.$grammarName"
      logger.lifecycle("Set namespace '$namespace' to $project")
    }

    ndkVersion = "24.0.8215888"

    defaultConfig {
      val rootProjDir = project.rootProject.projectDir.absolutePath
      val tsDir = "${rootProjDir}/tree-sitter-lib"

      externalNativeBuild {
        cmake {
          arguments("-DPROJECT_DIR=${rootProjDir}", "-DTS_DIR=${tsDir}")
        }
      }
    }

    externalNativeBuild {
      cmake {
        path = project.file("src/main/cpp/CMakeLists.txt")
        version = "3.22.1"
      }
    }
  }

  // avoid circular dependency
  if (project.projects.androidTreeSitter.name != project.name) {
    configurations.getByName("api").dependencies.add(
      project.projects.androidTreeSitter)
  }
}
