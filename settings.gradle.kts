pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm").version(kotlinVersion)
    }
}

include("lrucache")
include("vkstatistics")
include("refactoring")
