/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/\>.
 */

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