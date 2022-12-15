package com.itsaky.androidide.treesitter

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel.LIFECYCLE
import org.gradle.api.tasks.compile.JavaCompile

/**
 * Plugin applied to grammar modules.
 *
 * @author Akash Yadav
 */
class TsGrammarPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    generateGrammar(target)
  }

  fun generateGrammar(project: Project) {
    val grammarDir = project.file("src/main/cpp/grammar").absolutePath
    var tsCmd = project.rootProject.file("tree-sitter-lib/cli/build/release/tree-sitter").absolutePath
    if (!BUILD_TS_CLI_FROM_SOURCE) {
      tsCmd = "tree-sitter"
    }

    project.logger.log(LIFECYCLE, "Using '$tsCmd' to generate '${project.name}' grammar")

    project.executeCommand(grammarDir, tsCmd, "generate")
  }
}