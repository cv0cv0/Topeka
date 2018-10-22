import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.register
import org.gradle.plugin.use.PluginDependenciesSpec

val compileVersion = 28
val minVersion = 19
val targetVersion = 28
val studioVersion = "3.2.1"
val kotlinVersion = "1.2.71"
val supportVersion = "1.0.0"
val constraintVersion = "2.0.0-alpha2"
val instantVersion = "1.1.0"
val playVersion = "16.0.1"
val junitVersion = "4.12"
val runnerVersion = "1.1.0-beta02"
val espressoVersion = "3.1.0-beta02"

val buildTools = "com.android.tools.build:gradle:$studioVersion"
val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
val appcompat = "androidx.appcompat:appcompat:$supportVersion"
val recyclerView = "androidx.recyclerview:recyclerview:$supportVersion"
val cardView = "androidx.cardview:cardview:$supportVersion"
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
fun Project.registerClean() = tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}