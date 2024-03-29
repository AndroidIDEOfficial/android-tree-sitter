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

package com.itsaky.androidide.treesitter.jni

import com.sun.source.util.JavacTask
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.InputStream
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.tools.JavaFileObject.Kind.SOURCE
import javax.tools.SimpleJavaFileObject
import javax.tools.StandardLocation
import javax.tools.ToolProvider

/**
 * @author Akash Yadav
 */
abstract class GenerateNativeHeadersTask : DefaultTask() {

  @get:InputFiles
  abstract var srcFiles: FileTree

  @get:Input
  abstract val srcDirs: SetProperty<File>

  @get:InputFiles
  abstract var classPath: FileCollection

  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun generateHeaders() {
    val files = srcFiles.files
    val classPaths = classPath.files

    val compiler = ToolProvider.getSystemJavaCompiler()
    val fileManager =
      compiler.getStandardFileManager({}, Locale.ROOT, StandardCharsets.UTF_8)
    fileManager.setLocation(StandardLocation.CLASS_PATH, classPaths)
    fileManager.setLocation(StandardLocation.SOURCE_PATH, srcDirs.get())

    val results = files.flatMap { file ->

      val input = object : SimpleJavaFileObject(file.toURI(), SOURCE) {
        override fun openInputStream(): InputStream {
          return file.inputStream()
        }

        override fun getCharContent(ignoreEncodingErrors: Boolean
        ): CharSequence {
          return file.readText()
        }
      }

      val task =
        compiler.getTask(PrintWriter(System.out), fileManager, {}, emptyList(),
          emptyList(), listOf(input)) as JavacTask

      NativeHeaderGenerator.generate(task, file, outputDirectory.asFile.get())
    }

    @Suppress("LocalVariableName")
    run {
      val ts__onload_h = outputDirectory.file("ts__onload.h").get().asFile
      val ts_onload_h_contents = JNIWriter.generateJNIOnLoadHeader(results)
      ts__onload_h.writeText(ts_onload_h_contents)

      val ts__log_h = outputDirectory.file("ts__log.h").get().asFile
      val ts__log_h_contents = tsLogH(project.name)
      ts__log_h.writeText(ts__log_h_contents)
    }
  }

  private fun tsLogH(projectName: String): String =
    """
      /*
       *  This file is part of $projectName.
       *
       *  $projectName library is free software; you can redistribute it and/or
       *  modify it under the terms of the GNU Lesser General Public
       *  License as published by the Free Software Foundation; either
       *  version 2.1 of the License, or (at your option) any later version.
       *
       *  $projectName library is distributed in the hope that it will be useful,
       *  but WITHOUT ANY WARRANTY; without even the implied warranty of
       *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
       *  Lesser General Public License for more details.
       *
       *   You should have received a copy of the GNU General Public License
       *  along with $projectName.  If not, see <https://www.gnu.org/licenses/>.
       */

      #ifndef ATS_TS_LOG_H
      #define ATS_TS_LOG_H

      #define LOG_TAG "$projectName"

      #ifdef ANDROID
      #include <android/log.h>

      // For android

      #define LOGE(TAG, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
      #define LOGW(TAG, ...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
      #define LOGD(TAG, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
      #define LOGI(TAG, ...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
      #define LOGV(TAG, ...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)

      #else
      #include <cstdio>

      // for other systems
      // required for tests

      #define LOGE(TAG, ...) printf("[%s] ERROR: ", TAG); printf(__VA_ARGS__); printf("\n")
      #define LOGW(TAG, ...) printf("[%s] WARNING: ", TAG); printf(__VA_ARGS__); printf("\n")
      #define LOGD(TAG, ...) printf("[%s] DEBUG: ", TAG); printf(__VA_ARGS__); printf("\n")
      #define LOGI(TAG, ...) printf("[%s] INFO: ", TAG); printf(__VA_ARGS__); printf("\n")
      #define LOGV(TAG, ...) printf("[%s] VERBOSE: ", TAG); printf(__VA_ARGS__); printf("\n")

      #endif // ANDROID

      #endif //ATS_TS_LOG_H

    """.trimIndent()
}