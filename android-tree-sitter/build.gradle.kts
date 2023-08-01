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

plugins {
  id("com.android.library")
  id("com.vanniktech.maven.publish.base")
  id("android-tree-sitter.ts")
}

description = "Android Java bindings for Tree Sitter."

android {
  namespace = "com.itsaky.androidide.treesitter"
}

dependencies {
  testImplementation(projects.treeSitterAidl)
  testImplementation(projects.treeSitterJava)
  testImplementation(projects.treeSitterJson)
  testImplementation(projects.treeSitterKotlin)
  testImplementation(projects.treeSitterLog)
  testImplementation(projects.treeSitterXml)
  testImplementation(projects.treeSitterPython)
  testImplementation(libs.tests.google.truth)
  testImplementation(libs.tests.junit)
  testImplementation(libs.tests.robolectric)
}
