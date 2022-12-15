package com.itsaky.androidide.treesitter

import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * @author Akash Yadav
 */

fun Project.executeCommand(workingDir: String, vararg command: String) {
  val result = exec {
    workingDir(workingDir)
    commandLine(*command)
  }

  if (result.exitValue != 0) {
    throw GradleException("Failed to execute '${command.joinToString(" ")}'")
  }
}