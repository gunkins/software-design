pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm").version(kotlinVersion)
    }
}

include("lrucache")
