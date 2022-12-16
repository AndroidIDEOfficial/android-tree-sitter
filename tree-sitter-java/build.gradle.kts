import com.itsaky.androidide.treesitter.TreeSitterPlugin
import com.itsaky.androidide.treesitter.TsGrammarPlugin

plugins {
  id("com.android.library")
  id("com.vanniktech.maven.publish.base")
}

apply {
  plugin(TreeSitterPlugin::class.java)
  plugin(TsGrammarPlugin::class.java)
}

val rootProjDir: String = rootProject.projectDir.absolutePath
val tsDir = "${rootProjDir}/tree-sitter-lib"

android {
  namespace = "com.itsaky.androidide.treesitter.java"
  ndkVersion = "24.0.8215888"

  defaultConfig {
    externalNativeBuild { cmake { arguments("-DPROJECT_DIR=${rootProjDir}", "-DTS_DIR=${tsDir}") } }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  externalNativeBuild {
    cmake {
      path = file("src/main/cpp/CMakeLists.txt")
      version = "3.22.1"
    }
  }
}

dependencies { implementation(project(":android-tree-sitter")) }
