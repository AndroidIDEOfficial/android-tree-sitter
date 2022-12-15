plugins { `kotlin-dsl` }

repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
}

dependencies {
  implementation(gradleApi())
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}