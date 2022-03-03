plugins {
    application
    id("com.palantir.docker") version "0.32.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.github.gunkins"
version = "0.0.1"

repositories {
    mavenLocal()
}

dependencies {
    val ktorVersion: String by project
    val logbackVersion: String by project
    val exposedVersion: String by project
    val kodeinVersion: String by project
    val hikariVersion: String by project
    val postgresqlVersion: String by project
    val mockkVersion: String by project
    val assertjVersion: String by project

    implementation("io.ktor:ktor-server-netty:$ktorVersion") // ktor netty server
    implementation("ch.qos.logback:logback-classic:$logbackVersion") //logging
    implementation("io.ktor:ktor-server-core:$ktorVersion") // ktor server
    implementation("io.ktor:ktor-jackson:$ktorVersion") // jackson for ktor
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$kodeinVersion") // kodein for ktor

    // Exposed ORM library
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    implementation("com.zaxxer:HikariCP:$hikariVersion") // JDBC Connection Pool
    implementation("org.postgresql:postgresql:$postgresqlVersion") // JDBC Connector for PostgreSQL
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")

    implementation("ru.tinkoff.piapi:java-sdk-core:1.0-M3")
    implementation("ru.tinkoff.piapi:java-sdk:1.0-M3")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.testcontainers:postgresql:1.16.3")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

application {
    mainClass.set("com.github.gunkins.exchange.MainKt")
}

tasks {
    shadowJar {
        manifest {
            attributes("Main-Class" to application.mainClass)
        }
    }
}

docker {
    name = "${project.group}/${project.name}"

    val jarFile = tasks.shadowJar.get().archiveFile.get()

    files(jarFile)
    buildArgs(mapOf("JAR_FILE" to jarFile.asFile.name))
}