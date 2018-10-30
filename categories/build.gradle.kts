plugins {
    id("com.android.feature")
    `kotlin-android`
    `kotlin-android-extensions`
}

android {
    compileSdkVersion(compileVersion)
    defaultConfig {
        minSdkVersion(minVersion)
        targetSdkVersion(targetVersion)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
    }
    packagingOptions {
        pickFirst("LICENSE.txt")
        pickFirst("protobuf.meta")
    }

    generatePureSplits = true
    splits {
        density { isEnable = true }
    }
}

dependencies {
    implementation(project(":base"))
    testImplementation(junit)
    androidTestImplementation(runner)
    androidTestImplementation(espressoCore)
}
