import com.itsaky.androidide.treesitter.BuildForHostTask
import com.itsaky.androidide.treesitter.TreeSitterPlugin

plugins {
    id("com.android.library")
    id("com.vanniktech.maven.publish.base")
}

apply {
    plugin(TreeSitterPlugin::class.java)
}

group = "io.github.itsaky"
version = project.findProperty("VERSION_NAME")!!.toString()

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    externalNativeBuild {
        cmake {
            path = file("${cppDir}/CMakeLists.txt")
            version = "3.22.1"
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