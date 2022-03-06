plugins {
    application
    id("org.springframework.boot")
    kotlin("plugin.spring")
}
apply(plugin = "io.spring.dependency-management")

group = "com.github.gunkins"

dependencies {
    val hikariVersion: String by project
    val postgresqlVersion: String by project
    val mockkVersion: String by project

    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.zaxxer:HikariCP:$hikariVersion") // JDBC Connection Pool
    implementation("org.postgresql:postgresql:$postgresqlVersion") // JDBC Connector for PostgreSQL

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.zonky.test:embedded-database-spring-test:2.1.1")
    testImplementation("io.zonky.test:embedded-postgres:1.3.1")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

application {
    mainClass.set("com.github.gunkins.eventsoursing.ApplicationKt")
}
