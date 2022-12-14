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

fun executeCommand(workingDir: String, command: List<String>) {
    val result = exec {
        workingDir(workingDir)
        commandLine(command)
    }

    if (result.exitValue != 0) {
        throw GradleException("Failed to execute '${command.joinToString(" ")}'")
    }
}

task("buildForHost") {
    doLast {
        val workingDir = project.file("src/main/cpp").absolutePath
        executeCommand(workingDir, listOf("cmake", "--build", ".", "--clean-first"))
        executeCommand(workingDir, listOf("make"))

        val soName = "libandroid-tree-sitter.so"
        val so = File(workingDir, soName)
        val out = rootProject.file("build/host/${soName}")

        out.parentFile.mkdirs()

        so.renameTo(out)
    }
}

tasks.withType(JavaCompile::class.java) {
    dependsOn("buildForHost")
}