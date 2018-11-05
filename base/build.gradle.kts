plugins {
    id("com.android.feature")
    `kotlin-android`
    `kotlin-android-extensions`
}

android {
    compileSdkVersion(compileVersion)
    baseFeature(true)
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
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
    application(project(":app"))
    feature(project(":categories"))
    feature(project(":quiz"))

    api(stdlib)
    api(ankoCommons)
    api(ankoDesign)
    api(ktxCore)
    api(ktxFragment)
    api(appcompat)
    api(design)
    api(constraintLayout)
    api(supportV4)
    api(instantApp)
    implementation(playAuth)

    testImplementation(junit)
    androidTestImplementation(runner)
    androidTestImplementation(espressoCore)
}
