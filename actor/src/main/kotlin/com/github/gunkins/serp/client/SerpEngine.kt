package com.github.gunkins.serp.client

import com.fasterxml.jackson.annotation.JsonValue

enum class SerpEngine(@get:JsonValue val code: String) {
    GOOGLE("google"),
    YANDEX("yandex"),
    BING("bing")
}