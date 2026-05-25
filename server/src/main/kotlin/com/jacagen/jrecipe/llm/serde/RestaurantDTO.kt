package com.jacagen.jrecipe.llm.serde

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import kotlinx.datetime.LocalTime

// DTOs used only for Jackson deserialization

data class RestaurantDTO(
    val id: String,
    val name: String,
    val address: String,
    val open: WeeklyHoursDTO?,
    val tags: List<String>,
    val source: String
)

data class WeeklyHoursDTO(
    val hours: Map<String, List<OpeningPeriodDTO>>
)

data class OpeningPeriodDTO(
    @param:JsonDeserialize(using = LocalTimeDeserializer::class) val open: LocalTime,
    @param:JsonDeserialize(using = LocalTimeDeserializer::class) val close: LocalTime
)