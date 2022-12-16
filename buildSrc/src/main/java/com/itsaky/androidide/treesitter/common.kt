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