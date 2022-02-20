pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        val springVersion: String by settings

        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        id("org.springframework.boot") version springVersion
    }
}

include("lrucache")
include("vkstatistics")
include("refactoring")
include("mvc")
include("reactive")