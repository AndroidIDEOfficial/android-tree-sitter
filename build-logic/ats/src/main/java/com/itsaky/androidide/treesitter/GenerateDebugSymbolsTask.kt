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

import com.android.build.gradle.internal.core.Abi
import com.android.build.gradle.internal.ndk.NdkInfo
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * @author Akash Yadav
 */
abstract class GenerateDebugSymbolsTask : DefaultTask() {

  /**
   * The input directory which contains the unstripped native libraries.
   */
  @get:InputDirectory
  abstract val inputDirectory: DirectoryProperty

  /**
   * The output directory which where the debug symbols will be stored.
   */
  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  @get:Internal
  abstract val ndkInfo: Property<NdkInfo>

  @TaskAction
  fun generateDebugSymbols() {
    inputDirectory.asFileTree.files.forEach {
      generateDebugSymbolsForFile(it)
    }
  }

  private fun generateDebugSymbolsForFile(input: File?) {
    val file = if (input?.extension == "so") input else null ?: return
    val abi = checkNotNull(Abi.getByName(file.parentFile.name)) {
      "Unsupported ABI '${file.parentFile.name}' for file '$file'"
    }

    project.logger.info("Generating debug symbols file from: $file")

    val outputFile = outputDirectory.dir("" + abi.name.lowercase()).apply {
      val dir = get().asFile
      project.logger.debug("Creating output directory: {}", dir)
      dir.mkdirs()
    }.let {
      File(it.get().asFile, "${file.name}.debug_info").apply {
        project.logger.info("Output file for debug symbols: $this")
      }
    }

    val objcopyExe = ndkInfo.get().getObjcopyExecutable(abi)
    val cmdLine = arrayOf(
      objcopyExe.absolutePath,
      "--only-keep-debug",
      file.absolutePath,
      outputFile.absolutePath
    )

    project.logger.info("Executing cmd: ${cmdLine.joinToString(separator = " ")}")

    val output = ByteArrayOutputStream()
    val result = project.exec {
      commandLine(*cmdLine)
      standardOutput = output
      errorOutput = standardOutput
      isIgnoreExitValue = true
    }

    check(result.exitValue == 0) {
      "Failed to generate debug symbols for file: ${file}\n\n${
        String(output.toByteArray())
      }"
    }
  }
}