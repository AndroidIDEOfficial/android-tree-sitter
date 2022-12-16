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

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to build shared library for host OS.
 *
 * @author Akash Yadav
 */
abstract class BuildForHostTask : DefaultTask() {

  @Input var libName: String = ""

  @TaskAction
  fun buildForHost() {
    val cppDir = project.file("src/main/cpp").absolutePath
    val workingDir =
        project
            .file("${cppDir}/host-build")
            .apply {
              if (!exists()) {
                mkdirs()
              }
            }
            .absolutePath

    project.executeCommand(workingDir, "cmake", cppDir)
    project.executeCommand(workingDir, "make")

    if (libName.isEmpty()) {
      throw GradleException("'libName' must be specified for '${javaClass.simpleName}'")
    }

    val soName = "lib${libName}.so"
    val so = File(workingDir, soName)
    val out = project.rootProject.file("build/host/${soName}")

    out.parentFile.mkdirs()

    so.renameTo(out)
  }
}