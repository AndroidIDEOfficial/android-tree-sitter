/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.treesitter

import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel.LIFECYCLE
import org.gradle.api.tasks.TaskAction

/**
 * @author Akash Yadav
 */
class GenerateTreeSitterGrammarTask : DefaultTask() {

  @TaskAction
  fun generateGrammar() {
    val grammarDir = project.rootProject.file(
      "grammars/${project.name.substringAfterLast('-')}").absolutePath
    var tsCmd = project.rootProject.file(
      "tree-sitter-lib/cli/build/release/tree-sitter").absolutePath
    if (!BUILD_TS_CLI_FROM_SOURCE) {
      tsCmd = "tree-sitter"
    }

    project.logger.log(LIFECYCLE,
      "Using '$tsCmd' to generate '${project.name}' grammar")
    project.executeCommand(grammarDir, tsCmd, "generate")
  }
}