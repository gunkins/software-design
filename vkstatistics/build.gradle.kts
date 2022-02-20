plugins {
    application
}

group = "com.github.gunkins"

dependencies {
    val ktorVersion: String by project
    val logbackVersion: String by project
    val mockitoVersion: String by project

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoVersion")
}

application {
    mainClass.set("com.github.gunkins.vkstatistics.MainKt")
}