import com.android.build.gradle.BaseExtension
import com.itsaky.androidide.treesitter.BuildTsCliTask
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
    @Suppress("UnstableApiUsage")
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