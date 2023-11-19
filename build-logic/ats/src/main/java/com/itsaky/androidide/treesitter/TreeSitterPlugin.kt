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

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.BasePluginAccessor
import com.android.build.gradle.internal.plugins.LibraryPlugin
import com.android.build.gradle.tasks.ExternalNativeBuildTask
import com.itsaky.androidide.treesitter.jni.GenerateNativeHeadersTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import java.util.Locale

/**
 * Marker plugin.
 *
 * @author Akash Yadav
 */
class TreeSitterPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    target.run {
      val baseExtention = extensions.getByType(BaseExtension::class.java)
      val pluginType = if (plugins.hasPlugin(
          "com.android.application")
      ) AppPlugin::class.java else LibraryPlugin::class.java
      val dslServices = plugins.getPlugin(pluginType)
        .let { BasePluginAccessor.getDslServices(it) }

      @Suppress("DEPRECATION")
      val ndkPlatform = dslServices.sdkComponents.map {
        it.versionedNdkHandler(
          baseExtention.compileSdkVersion ?: throw kotlin.IllegalStateException(
            "compileSdkVersion not set in the android configuration"),
          baseExtention.ndkVersion,
          baseExtention.ndkPath).ndkPlatform.getOrThrow()
      }

      extensions.getByType(AndroidComponentsExtension::class.java).apply {
        onVariants { variant ->
          val variantName = variant.name.replaceFirstChar { name ->
            if (name.isLowerCase()) name.titlecase(
              Locale.ROOT) else name.toString()
          }

          @Suppress("UnstableApiUsage")
          val generateDebugSymbolsTask =
            tasks.register("generateDebugSymbols$variantName",
              GenerateDebugSymbolsTask::class.java) {

              dependsOn(tasks.getByName("merge${variantName}NativeLibs"))

              this.inputDirectory.set(
                variant.artifacts.get(SingleArtifact.MERGED_NATIVE_LIBS))
              this.ndkInfo.set(ndkPlatform.get().ndkInfo)
            }

          variant.sources.assets?.addGeneratedSourceDirectory(
            generateDebugSymbolsTask, GenerateDebugSymbolsTask::outputDirectory)

          val cleanHostBuild =
            tasks.register("cleanHostBuild$variantName", Delete::class.java) {
              delete("src/main/cpp/host-build")
            }

          val generateNativeHeadersTask =
            tasks.register("generateNativeHeaders$variantName",
              GenerateNativeHeadersTask::class.java) {

              srcFiles = baseExtention.sourceSets.getByName("main").java.getSourceFiles()
              classPath = variant.compileClasspath
              outputDirectory.set(
                project.layout.buildDirectory.dir("generated/native_headers"))
            }

          val buildForHost = tasks.register("buildForHost$variantName",
            BuildForHostTask::class.java) {

            dependsOn(rootProject.tasks.getByName("buildTreeSitter"))
            dependsOn(generateNativeHeadersTask)

            libName = project.name
          }

          tasks.withType(ExternalNativeBuildTask::class.java) {
            dependsOn(buildForHost)
          }

          tasks.named("clean") {
            dependsOn(cleanHostBuild)
          }
        }
      }
    }
  }
}