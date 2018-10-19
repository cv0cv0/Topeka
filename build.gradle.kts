// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        aliyun()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$studioVersion")
        classpath(kotlin("gradle-plugin", kotlinVersion))

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        aliyun()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}