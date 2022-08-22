import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

group = "top.colter"
version = "0.1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-okhttp:2.1.0") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}