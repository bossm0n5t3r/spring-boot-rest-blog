package me.bossm0n5t3r.blog.common.configuration

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

inline fun <reified T> ObjectMapper.readValueWithTypeReference(s: String): T =
    this.readValue(s, object : TypeReference<T>() {})
