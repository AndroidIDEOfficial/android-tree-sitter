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
    id("com.android.application")
}

android {
    namespace = "com.itsaky.androidide.androidtreesitter"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.itsaky.androidide.androidtreesitter"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.constraintlayout)

    implementation(projects.androidTreeSitter)
    implementation(projects.treeSitterJava)
    implementation(projects.treeSitterJson)
    implementation(projects.treeSitterKotlin)
    implementation(projects.treeSitterLog)
    implementation(projects.treeSitterPython)
    implementation(projects.treeSitterXml)

    testImplementation(libs.tests.junit)
    androidTestImplementation(libs.tests.androidx.ext.junit)
    androidTestImplementation(libs.tests.androidx.espresso.core)
}