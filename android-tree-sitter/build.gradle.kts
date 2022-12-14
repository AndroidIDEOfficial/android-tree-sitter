import com.itsaky.androidide.treesitter.BuildForHostTask
import com.itsaky.androidide.treesitter.TreeSitterPlugin

plugins {
    id("com.android.library")
    id("com.vanniktech.maven.publish.base")
}

apply {
    plugin(TreeSitterPlugin::class.java)
}

val rootProjDir: String = rootProject.projectDir.absolutePath
val tsDir = "${rootProjDir}/tree-sitter-lib"
val cppDir = project.file("src/main/cpp")

android {
    namespace = "com.itsaky.androidide.treesitter"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("junit:junit:4.13.2")
}

tasks.register("buildForHost", BuildForHostTask::class.java) {
    libName = "android-tree-sitter"
}

tasks.withType(JavaCompile::class.java) {
    dependsOn("buildForHost")
}