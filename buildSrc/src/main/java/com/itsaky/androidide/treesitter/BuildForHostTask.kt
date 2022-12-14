package com.itsaky.androidide.treesitter

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to build shared library for host OS.
 *
 * @author Akash Yadav
 */
abstract class BuildForHostTask : DefaultTask() {

  @Input var libName: String = ""

  @TaskAction
  fun buildForHost() {
    val cppDir = project.file("src/main/cpp").absolutePath
    val workingDir =
        project
            .file("${cppDir}/host-build")
            .apply {
              if (!exists()) {
                mkdirs()
              }
            }
            .absolutePath

    project.executeCommand(workingDir, listOf("cmake", cppDir))
    project.executeCommand(workingDir, listOf("make"))

    if (libName.isEmpty()) {
      throw GradleException("'libName' must be specified for '${javaClass.simpleName}'")
    }

    val soName = "lib${libName}.so"
    val so = File(workingDir, soName)
    val out = project.rootProject.file("build/host/${soName}")

    out.parentFile.mkdirs()

    so.renameTo(out)
  }

  private fun Project.executeCommand(workingDir: String, command: List<String>) {
    val result = exec {
      workingDir(workingDir)
      commandLine(command)
    }

    if (result.exitValue != 0) {
      throw GradleException("Failed to execute '${command.joinToString(" ")}'")
    }
  }
}
