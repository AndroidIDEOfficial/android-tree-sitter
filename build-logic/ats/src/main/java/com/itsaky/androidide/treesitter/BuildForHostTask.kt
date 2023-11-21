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
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Gradle task to build shared library for host OS.
 *
 * @author Akash Yadav
 */
abstract class BuildForHostTask : DefaultTask() {

  @get:InputFiles
  abstract val cppDir: DirectoryProperty

  @get:OutputFile
  abstract val outputFile: RegularFileProperty

  @TaskAction
  fun buildForHost() {
    val cppDir = cppDir.get().asFile
    val workingDir = cppDir.resolve("host-build").apply {
      if (!exists()) {
        mkdirs()
      }
    }.absolutePath

    val nativeHeaderDirPath =
      project.layout.buildDirectory.dir("generated/native_headers")
        .get().asFile.absolutePath

    project.executeCommand(workingDir, "cmake", cppDir.absolutePath, "-DAUTOGEN_HEADERS=$nativeHeaderDirPath")
    project.executeCommand(workingDir, "make")

    if (project.name.isEmpty()) {
      throw GradleException(
        "Project name must be specified for '${javaClass.simpleName}'")
    }

    val (so, out) = getOutputFile(project)
    out.parentFile.mkdirs()
    so.renameTo(out)
  }

  companion object {

    internal fun getOutputFile(project: Project): Pair<File, File> {
      return project.run {
        val cppDir = project.file("src/main/cpp").absolutePath
        val workingDir = project.file("${cppDir}/host-build").apply {
          if (!exists()) {
            mkdirs()
          }
        }.absolutePath

        val soName = "lib${project.name}.so"
        val so = File(workingDir, soName)
        val out = project.rootProject.file("build/host/${soName}")
        Pair(so, out)
      }
    }
  }
}
