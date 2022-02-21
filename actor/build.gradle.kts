plugins {
    application
}

group = "com.github.gunkins"

dependencies {
    val ktorVersion: String by project
    val logbackVersion: String by project
    val assertjVersion: String by project
    val mockkVersion: String by project
    val akkaVersion = "2.6.18"

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.10.2")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-network:$ktorVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")


    implementation("com.typesafe.akka:akka-bom_3:$akkaVersion")
    implementation("com.typesafe.akka:akka-actor-typed_3:$akkaVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

application {
    mainClass.set("com.github.gunkins.serp.MainKt")
}