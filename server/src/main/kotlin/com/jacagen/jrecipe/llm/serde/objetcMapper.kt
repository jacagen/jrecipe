@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.llm.serde

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.datetime.LocalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val serdeModule = SimpleModule().apply { // name something different
    addDeserializer(Uuid::class.java, UuidDeserializer())
    addDeserializer(LocalTime::class.java, LocalTimeDeserializer())
}

// This is public so that it is usable in tests.  I don't like this idea.
val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(serdeModule)