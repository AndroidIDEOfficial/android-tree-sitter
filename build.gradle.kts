/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/\>.
 */

import com.android.build.gradle.BaseExtension
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
  id("com.android.application") version "7.4.0-rc01" apply false
  id("com.android.library") version "7.4.0-rc01" apply false
  id("com.vanniktech.maven.publish.base") version "0.22.0" apply false
}

fun Project.configureBaseExtension() {
  extensions.findByType(BaseExtension::class)?.run {
    compileSdkVersion(33)

    defaultConfig {
      minSdk = 21
      targetSdk = 33
      versionCode = project.findProperty("VERSION_CODE")!!.toString().toInt()
      versionName = project.findProperty("VERSION_NAME")!!.toString()
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
    }
  }
}

subprojects {
  plugins.withId("com.android.application") { configureBaseExtension() }
  plugins.withId("com.android.library") { configureBaseExtension() }

  plugins.withId("com.vanniktech.maven.publish.base") {
    configure<MavenPublishBaseExtension> {
      group = "io.github.itsaky"
      version = project.findProperty("VERSION_NAME")!!
      pomFromGradleProperties()
      publishToMavenCentral(SonatypeHost.S01)
      signAllPublications()
      configure(AndroidSingleVariantLibrary(publishJavadocJar = false))
    }
  }
}

tasks.register<Delete>("clean").configure {
  delete(rootProject.buildDir)
  delete(rootProject.file("build/host"))
  delete(rootProject.file("tree-sitter-lib/cli/build"))
}