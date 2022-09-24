plugins {
    `kotlin-dsl`
    kotlin ("jvm") version "1.6.21"
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.12.0")
}

repositories {
    mavenLocal()
    mavenCentral()
}