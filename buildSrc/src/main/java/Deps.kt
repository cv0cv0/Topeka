import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.register
import org.gradle.plugin.use.PluginDependenciesSpec

val compileVersion = 28
val minVersion = 19
val targetVersion = 28
val studioVersion = "3.3.0-beta04"
val kotlinVersion = "1.3.10"
val ankoVersion = "0.10.8"
val ktxCoreVersion="1.0.1"
val appcompatVersion="1.0.2"
val supportVersion = "1.0.0"
val constraintVersion = "1.1.3"
val instantVersion = "1.1.0"
val playVersion = "16.0.1"
val junitVersion = "4.12"
val runnerVersion = "1.1.0"
val espressoVersion = "3.1.0"

val buildTools = "com.android.tools.build:gradle:$studioVersion"
val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
val ankoCommons = "org.jetbrains.anko:anko-commons:$ankoVersion"
val ankoDesign = "org.jetbrains.anko:anko-design:$ankoVersion"
val ktxCore="androidx.core:core-ktx:$ktxCoreVersion"
val ktxFragment="androidx.fragment:fragment-ktx:$supportVersion"
val appcompat = "androidx.appcompat:appcompat:$appcompatVersion"
val recyclerView = "androidx.recyclerview:recyclerview:$supportVersion"
val design = "com.google.android.material:material:$supportVersion"
val supportV4 = "androidx.legacy:legacy-support-v4:$supportVersion"
val constraintLayout = "androidx.constraintlayout:constraintlayout:$constraintVersion"
val instantApp = "com.google.android.instantapps:instantapps:$instantVersion"
val playAuth = "com.google.android.gms:play-services-auth:$playVersion"
val junit = "junit:junit:$junitVersion"
val runner = "androidx.test:runner:$runnerVersion"
val espressoCore = "androidx.test.espresso:espresso-core:$espressoVersion"

val PluginDependenciesSpec.`android-application`
    get() = id("com.android.application")
val PluginDependenciesSpec.`kotlin-android`
    get() = id("kotlin-android")
val PluginDependenciesSpec.`kotlin-android-extensions`
    get() = id("kotlin-android-extensions")
val PluginDependenciesSpec.`kotlin-kapt`
    get() = id("kotlin-kapt")

fun RepositoryHandler.aliyun() = maven("https://maven.aliyun.com/repository/public")
fun Project.cleanTask() = tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}