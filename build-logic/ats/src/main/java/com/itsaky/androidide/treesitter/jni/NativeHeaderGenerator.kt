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

import com.sun.source.tree.ClassTree
import com.sun.source.util.JavacTask
import com.sun.source.util.TreePathScanner
import com.sun.source.util.Trees
import java.io.File
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

/**
 * @author Akash Yadav
 */
object NativeHeaderGenerator {

  private const val ANNOTATION_GNH = "GenerateNativeHeaders"
  private const val ANNOTATION_GNH_fqn =
    "com.itsaky.androidide.treesitter.annotations.$ANNOTATION_GNH"

  fun generate(task: JavacTask, file: File, outputDirectory: File
  ) {

    val trees = task.parse()
    task.analyze()

    trees.forEach { cu ->
      val visitor = object : TreePathScanner<Unit, Unit>() {
        override fun visitClass(node: ClassTree, p: Unit) {
          node.members.filterIsInstance<ClassTree>().forEach {
            visitClass(it, p)
          }

          val element = Trees.instance(task).getElement(currentPath)
          if (element is TypeElement) {
            handleElement(JNIWriter(task.types, task.elements), element, file,
              outputDirectory)
          }
        }
      }

      visitor.scan(cu, Unit)
    }
  }

  private fun handleElement(writer: JNIWriter, type: TypeElement, file: File,
                            outputDirectory: File
  ) {
    type.enclosedElements.filterIsInstance<TypeElement>().forEach {
      handleElement(writer, it, file, outputDirectory)
    }

    val annotation = type.annotationMirrors.filter {
      (it.annotationType.asElement() as TypeElement).qualifiedName.contentEquals(
        ANNOTATION_GNH_fqn)
    }.ifEmpty {
      return
    }.first()

    val valueFunc =
      (annotation.annotationType.asElement() as TypeElement).enclosedElements.first() as ExecutableElement

    val fileName = annotation.elementValues!![valueFunc]!!.value.toString()
    val result = writer.generate(type)

    if (outputDirectory.exists()) {
      outputDirectory.delete()
    }

    outputDirectory.mkdirs()

    val methodHeaders = File(outputDirectory, "ts_$fileName.h")
    val methodSignatures = File(outputDirectory, "ts_${fileName}_sigs.h")

    methodHeaders.writeText(result.first)
    methodSignatures.writeText(result.second)
  }
}