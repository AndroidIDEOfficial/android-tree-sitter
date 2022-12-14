import com.itsaky.androidide.treesitter.TreeSitterPlugin

plugins {
  id("com.android.library")
  id("com.vanniktech.maven.publish.base")
}

apply {
  plugin(TreeSitterPlugin::class.java)
}

android {
  namespace = "com.itsaky.androidide.treesitter.java"

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {
  implementation(project(":android-tree-sitter"))
}

tasks.register("buildForHost", com.itsaky.androidide.treesitter.BuildForHostTask::class.java) {
  libName = "tree-sitter-java"
}

tasks.withType(JavaCompile::class.java) {
  dependsOn("buildForHost")
}