package com.itsaky.androidide.treesitter

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.create

/**
 * Marker plugin.
 *
 * @author Akash Yadav
 */
class TreeSitterPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.let { project ->
      project.tasks.register("buildForHost", BuildForHostTask::class.java) {
        libName = project.name
      }

      project.tasks.create("cleanHostBuild", type = Delete::class) {
        delete("src/main/cpp/host-build")
      }

      project.tasks.named("clean").configure { dependsOn("cleanHostBuild") }
      project.tasks.withType(JavaCompile::class.java) { dependsOn("buildForHost") }
    }
  }
}
