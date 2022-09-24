import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.triviamasters.BuildConfigTask

plugins {
    val joobyVersion = "2.16.1"
    val kotlinVersion = "1.6.21"

    application
    kotlin ("jvm") version kotlinVersion
    id("io.jooby.run") version joobyVersion
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.google.osdetector") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "app"
version = "1.0.0"

val joobyVersion = "2.16.1"
val koinVersion = "3.2.1"
val kotlinVersion = "1.6.21"

application {
    mainClass.set("com.github.triviamasters.AppKt")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("io.jooby:jooby-bom:$joobyVersion")
    }
}

dependencies {
    implementation("io.jooby:jooby-utow")
    implementation("io.jooby:jooby-jackson")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("ch.qos.logback:logback-classic")
    implementation("io.insert-koin:koin-core:$koinVersion")

    testImplementation ("io.insert-koin:koin-test:$koinVersion")
    testImplementation ("org.junit.jupiter:junit-jupiter-api")
    testImplementation ("org.junit.jupiter:junit-jupiter-engine")
    testImplementation ("io.jooby:jooby-test")
    testImplementation ("com.squareup.okhttp3:okhttp")
}

tasks.register<BuildConfigTask>(BuildConfigTask.taskName) {
    dev {
        buildType = "DEV"
        useMocks = true
    }

    prod {
        buildType = "PROD"
        useMocks = false
    }
}
tasks.named("compileKotlin") {
    dependsOn(BuildConfigTask.taskName)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.javaParameters = true
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}