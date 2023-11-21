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

import com.android.build.api.artifact.SingleArtifact.MERGED_NATIVE_LIBS
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.ndk.NdkPlatform
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.BasePluginAccessor
import com.android.build.gradle.internal.plugins.LibraryPlugin
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.android.build.gradle.tasks.ExternalNativeBuildTask
import com.itsaky.androidide.treesitter.jni.GenerateNativeHeadersTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import java.util.Locale

/**
 * Marker plugin.
 *
 * @author Akash Yadav
 */
class TreeSitterPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    target.run {
      val cppDir = project.file("src/main/cpp")
      val nativeHeadersDir =
        project.layout.buildDirectory.dir("generated/native_headers")

      val buildForHost =
        tasks.register("buildForHost", BuildForHostTask::class.java) {
          // Build tree-sitter library first
          dependsOn(rootProject.tasks.getByName("buildTreeSitter"))

          // Generate the tree sitter parser from grammar.js
          tasks.findByName("generateTreeSitterGrammar")?.also { dependsOn(it) }

          this.cppDir.set(cppDir)
          this.outputFile.set(BuildForHostTask.getOutputFile(project).second)
        }

      val cleanHostBuild =
        tasks.register("cleanHostBuild", Delete::class.java) {
          delete(cppDir.resolve("host-build"))
        }

      tasks.named("clean") {
        dependsOn(cleanHostBuild)
      }

      tasks.withType(ExternalNativeBuildTask::class.java) {
        dependsOn(buildForHost)
      }

      tasks.withType(Test::class.java) {
        dependsOn(buildForHost)

        if (!project.name.startsWith("tree-sitter-")) {
          rootProject.subprojects.filter {
            it.name.startsWith("tree-sitter-")
          }.forEach { grammarProject ->
            dependsOn(grammarProject.tasks.withType(BuildForHostTask::class.java))
          }
        }
      }

      val baseExtention = extensions.getByType(BaseExtension::class.java)

      baseExtention.defaultConfig.externalNativeBuild.cmake.arguments(
        "-DAUTOGEN_HEADERS=${nativeHeadersDir.get().asFile.absolutePath}"
      )

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
          configureVariant(variant, ndkPlatform, baseExtention, buildForHost)
        }
      }
    }
  }

  private fun Project.configureVariant(variant: Variant,
                                       ndkPlatform: Provider<NdkPlatform>,
                                       baseExtention: BaseExtension,
                                       buildForHost: TaskProvider<BuildForHostTask>
  ) {
    val variantName = variant.name.replaceFirstChar { name ->
      if (name.isLowerCase()) name.titlecase(Locale.ROOT) else name.toString()
    }
    configureGenDbgSymsTask(variantName, variant, ndkPlatform)
    configureGenNativeHeadersTask(variantName, baseExtention, variant,
      buildForHost)
  }

  @Suppress("UnstableApiUsage")
  private fun Project.configureGenNativeHeadersTask(variantName: String,
                                                    baseExtention: BaseExtension,
                                                    variant: Variant,
                                                    buildForHost: TaskProvider<BuildForHostTask>
  ) {

    val generateNativeHeadersTask =
      tasks.register("generateNativeHeaders$variantName",
        GenerateNativeHeadersTask::class.java) {

        val javaSrc = baseExtention.sourceSets.getByName("main").java
        srcFiles = javaSrc.getSourceFiles()
        classPath = variant.compileClasspath
        srcDirs.set(javaSrc.srcDirs)
        outputDirectory.set(
          project.layout.buildDirectory.dir("generated/native_headers"))
      }

    buildForHost.dependsOn(generateNativeHeadersTask)
  }

  private fun Project.configureGenDbgSymsTask(variantName: String,
                                              variant: Variant,
                                              ndkPlatform: Provider<NdkPlatform>
  ) {
    @Suppress("UnstableApiUsage")
    val generateDebugSymbolsTask =
      tasks.register("generateDebugSymbols$variantName",
        GenerateDebugSymbolsTask::class.java) {

        dependsOn(tasks.getByName("merge${variantName}NativeLibs"))

        this.inputDirectory.set(variant.artifacts.get(MERGED_NATIVE_LIBS))
        this.ndkInfo.set(ndkPlatform.get().ndkInfo)
      }

    variant.sources.assets?.addGeneratedSourceDirectory(
      generateDebugSymbolsTask, GenerateDebugSymbolsTask::outputDirectory)
  }
}