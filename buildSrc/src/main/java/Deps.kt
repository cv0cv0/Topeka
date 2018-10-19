import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.register

val compileVersion = 28
val minVersion = 21
val targetVersion = 28
val studioVersion = "3.2.1"
val kotlinVersion = "1.2.71"
val supportVersion = "1.0.0"
val constraintVersion = "2.0.0-alpha2"

val appcompatSupport="androidx.appcompat:appcompat:$supportVersion"
val constraintLayout="androidx.constraintlayout:constraintlayout:$constraintVersion"

fun RepositoryHandler.aliyun() = maven("https://maven.aliyun.com/repository/public")