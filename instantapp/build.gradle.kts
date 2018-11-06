import com.android.build.gradle.BaseExtension

plugins {
    id("com.android.instantapp")
}

android {
    this as BaseExtension
    defaultConfig {
        missingDimensionStrategy("delivery","instantapp")
    }
}

dependencies {
    implementation(project(":base"))
    implementation(project(":categories"))
    implementation(project(":quiz"))
}
