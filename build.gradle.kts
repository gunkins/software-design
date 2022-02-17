import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    id("org.jetbrains.kotlin.jvm") apply false
}

subprojects {
    version = "1.0.0"

    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    dependencies {
        "testImplementation"(kotlin("test"))
    }

    tasks.withType<Test>().all {
        useJUnitPlatform()
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}