plugins {
    id("org.springframework.boot")
    kotlin("plugin.spring")
    application
}
apply(plugin = "io.spring.dependency-management")

group = "com.github.gunkins"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

application {
    mainClass.set("com.github.gunkins.reactive.ApplicationKt")
}