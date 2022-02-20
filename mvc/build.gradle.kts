plugins {
    id("org.springframework.boot")
    kotlin("plugin.spring")
    application
}
apply(plugin = "io.spring.dependency-management")

group = "com.github.gunkins"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.github.gunkins.mvc.ApplicationKt")
}