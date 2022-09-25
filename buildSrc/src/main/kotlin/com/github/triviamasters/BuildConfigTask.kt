package com.github.triviamasters

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.squareup.kotlinpoet.*
import org.gradle.api.Project
import org.gradle.api.file.Directory
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.properties.Delegates

class BuildConfigBuilder {
    lateinit var buildType: String
    var useMocks by Delegates.notNull<Boolean>()
}

abstract class BuildConfigTask : DefaultTask() {

    private lateinit var devBuilder: BuildConfigBuilder
    private lateinit var prodBuilder: BuildConfigBuilder

    @TaskAction
    fun buildConfigFile() {
        val buildType = project.property("build")

        val builder : BuildConfigBuilder = when (buildType) {
            "DEV" -> devBuilder
            "PROD" -> prodBuilder
            else -> BuildConfigBuilder()
        }

        val configClass = ClassName("com.github.triviamasters.generated", "BuildConfig")

        val file = FileSpec.builder("com.github.triviamasters.generated", "BuildConfig")
            .addType(
                TypeSpec.objectBuilder(configClass.simpleName)
                    .addProperty(buildTypeSpec(builder))
                    .addProperty(useMocksSpec(builder))
                    .build()
            ).build()

        val output = Paths.get(project.projectDir.absolutePath, "src", "main", "kotlin")
        val outputDir = Paths.get(output.toString(), file.packageName.replace('.', '/'))

        project.delete(outputDir)

        file.writeTo(output)
    }

    private fun buildTypeSpec(builder: BuildConfigBuilder) = PropertySpec
        .builder("buildType", String::class)
        .initializer("%S", builder.buildType)
        .build()

    private fun useMocksSpec(builder: BuildConfigBuilder) = PropertySpec
        .builder("useMocks", Boolean::class)
        .initializer("%L", builder.useMocks)
        .build()

    fun dev(configure: BuildConfigBuilder.() -> Unit) {
        devBuilder = BuildConfigBuilder()
        devBuilder.configure()
    }

    fun prod(configure: BuildConfigBuilder.() -> Unit) {
        prodBuilder = BuildConfigBuilder()
        prodBuilder.configure()
    }

    companion object {
        const val taskName = "buildConfigFile"
    }
}