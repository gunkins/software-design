plugins {
    application
}

dependencies {
    val sqliteVersion: String by project
    val jettyVersion: String by project

    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")
    implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-servlet:$jettyVersion")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("ru.akirakozov.sd.refactoring.Main")
}