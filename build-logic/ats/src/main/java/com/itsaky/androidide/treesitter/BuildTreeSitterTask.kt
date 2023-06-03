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

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task for building the tree-sitter lib.
 *
 * @author Akash Yadav
 */
abstract class BuildTreeSitterTask : DefaultTask() {

  @TaskAction
  fun buildTsCli() {
    if (!BUILD_TS_CLI_FROM_SOURCE) {
      project.logger.warn(
        "Skipping tree-sitter-cli build as $ENV_TS_CLI_BUILD_FROM_SOURCE is set to 'false'")
      return
    }

    val cliDir = project.rootProject.file("tree-sitter-lib/cli")
    val buildDir = File(cliDir, "build")

    val cmd = arrayOf("cargo", "b", "--target-dir", buildDir.absolutePath, "--release")

    project.logger.info(
      "Building tree-sitter-cli with command ${cmd.joinToString(separator = " ")}")

    project.executeCommand(cliDir.absolutePath, command = cmd)
  }
}
