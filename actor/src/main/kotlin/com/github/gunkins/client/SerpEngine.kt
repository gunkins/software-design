package com.github.gunkins.client

import com.fasterxml.jackson.annotation.JsonValue

enum class SerpEngine(@get:JsonValue val code: String) {
    GOOGLE("google"),
    YANDEX("yandex"),
    BING("bing")
}