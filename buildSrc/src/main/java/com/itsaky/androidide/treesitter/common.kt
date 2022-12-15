package com.itsaky.androidide.treesitter

import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * @author Akash Yadav
 */

const val ENV_TS_CLI_BUILD_FROM_SOURCE = "TS_CLI_BUILD_FROM_SOURCE"

val BUILD_TS_CLI_FROM_SOURCE by lazy {
  System.getenv(ENV_TS_CLI_BUILD_FROM_SOURCE)?.toBoolean() ?: true
}

fun Project.executeCommand(workingDir: String, vararg command: String) {
  val result = exec {
    workingDir(workingDir)
    commandLine(*command)
    standardOutput = System.out
    errorOutput = System.err
  }

  if (result.exitValue != 0) {
    throw GradleException("Failed to execute '${command.joinToString(" ")}'")
  }
}