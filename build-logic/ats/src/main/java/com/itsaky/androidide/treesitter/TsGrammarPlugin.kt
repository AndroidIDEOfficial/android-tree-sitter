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
    target.run {

      tasks.register("generateTreeSitterGrammar") {
        doLast {
          generateGrammar(project = project)
        }
      }

      tasks.withType(JavaCompile::class.java) {
        dependsOn("generateTreeSitterGrammar")
      }
    }
  }

  fun generateGrammar(project: Project) {
    val grammarDir = project.rootProject.file("grammars/${project.name.substringAfterLast('-')}").absolutePath
    var tsCmd =
        project.rootProject.file("tree-sitter-lib/cli/build/release/tree-sitter").absolutePath
    if (!BUILD_TS_CLI_FROM_SOURCE) {
      tsCmd = "tree-sitter"
    }

    val buildTimestamp = File(project.buildDir, ".grammar_build")
    val parserC = File(grammarDir, "src/parser.c")
    val alreadyBuilt =
        buildTimestamp.exists() &&
            parserC.exists() &&
            buildTimestamp.lastModified() >= parserC.lastModified()

    if (alreadyBuilt) {
      project.logger.log(
          LIFECYCLE, "Skipping grammar build for '${project.name}' as it is already built")
      return
    }

    project.logger.log(LIFECYCLE, "Using '$tsCmd' to generate '${project.name}' grammar")
    project.executeCommand(grammarDir, tsCmd, "generate")
    if (buildTimestamp.exists()) {
      buildTimestamp.setLastModified(System.currentTimeMillis())
    } else {
      buildTimestamp.parentFile.mkdirs()
      buildTimestamp.bufferedWriter().use { it.write("Tree Siter ${project.name} build timestamp") }
    }
  }
}