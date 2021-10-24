plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.jetbrains.compose") version "1.1.0-alpha05"
}

javafx {
    version = "11.0.2"
    modules = listOf("javafx.controls", "javafx.graphics")
}

group = "com.github.gunkins"

repositories {
    mavenCentral()
}

dependencies {
    implementation("no.tornado:tornadofx:1.7.20")
    implementation(compose.desktop.currentOs)
}

application {
    mainClass.set("com.github.gunkins.bridge.MainKt")
}