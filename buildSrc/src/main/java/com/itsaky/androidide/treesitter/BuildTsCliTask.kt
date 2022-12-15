package com.itsaky.androidide.treesitter

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * A task to build tree-sitter-cli from source.
 *
 * @author Akash Yadav
 */
abstract class BuildTsCliTask : DefaultTask() {

  @TaskAction
  fun buildTsCli() {


    if (!BUILD_TS_CLI_FROM_SOURCE) {
      project.logger.warn("Skipping tree-sitter-cli build as $ENV_TS_CLI_BUILD_FROM_SOURCE is set to 'false'")
      return
    }

    var cliDir = project.rootProject.file("tree-sitter-lib/cli")
    var buildDir = File(cliDir, "build")

    val cmd = arrayOf("cargo", "b", "--target-dir", buildDir.absolutePath, "--release")

    project.logger.info(
        "Building tree-sitter-cli with command ${cmd.joinToString(separator = " ")}")

    project.executeCommand(cliDir.absolutePath, command = cmd)
  }
}
