plugins {
    `android-application`
}

android {
    compileSdkVersion(compileVersion)
    defaultConfig {
        minSdkVersion(minVersion)
        targetSdkVersion(targetVersion)
        missingDimensionStrategy("delivery","app")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":base"))
    implementation(project(":categories"))
    implementation(project(":quiz"))
}