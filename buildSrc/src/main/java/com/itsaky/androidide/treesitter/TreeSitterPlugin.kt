package com.itsaky.androidide.treesitter

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.create
import java.io.File

/**
 * Marker plugin.
 *
 * @author Akash Yadav
 */
class TreeSitterPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.let { project ->
      project.tasks.register("buildForHost", BuildForHostTask::class.java) {
        libName = project.name
      }

      project.tasks.create("cleanHostBuild", type = Delete::class) {
        delete("src/main/cpp/host-build")
      }

      project.tasks.named("clean").configure { dependsOn("cleanHostBuild") }
      project.tasks.withType(JavaCompile::class.java) { dependsOn("buildForHost") }
    }

    buildTsCli(project = target)
  }

  private fun buildTsCli(project: Project) {
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
