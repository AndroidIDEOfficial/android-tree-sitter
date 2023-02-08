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

import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate

/**
 * @author Akash Yadav
 */

private var _versionCode: Int? = null

val Project.projectVersionCode : Int
  get() = _versionCode ?: findVersionCode().also { _versionCode = it }

private fun Project.findVersionCode() : Int {
  val version = rootProject.version.toString()
  val regex = Regex("^v\\d+\\.?\\d+\\.?\\d+")

  return regex.find(version)?.value?.substring(1)?.replace(".", "")?.toInt()?.also {
    logger.warn("Version code is '$it' (from version ${rootProject.version}).")
  }
    ?: throw IllegalStateException(
      "Invalid version string '$version'. Version names must be SEMVER with 'v' prefix"
    )
}