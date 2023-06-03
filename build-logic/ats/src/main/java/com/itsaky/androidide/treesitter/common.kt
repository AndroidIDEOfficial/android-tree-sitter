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

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate

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