package com.jacagen.jrecipe.llm.serde

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.jacagen.jrecipe.model.*
import kotlinx.datetime.LocalTime

// DTOs used only for Jackson deserialization

data class RestaurantDTO(
    val id: String,
    val name: String,
    val address: String,
    val open: WeeklyHoursDTO?,
    val tags: List<String>,
    val source: String
) {
    fun toRestaurant(): Restaurant = Restaurant(
        id = id,
        name = name,
        address = address,
        open = open?.toWeeklyHours() ?: WeeklyHours(emptyMap()),
        tags = tags.toSet(),
        source = EntitySource.valueOf(source)
    )
}

data class WeeklyHoursDTO(
    val hours: Map<String, List<OpeningPeriodDTO>>
) {
    fun toWeeklyHours(): WeeklyHours =
        WeeklyHours(hours.mapKeys { (dayOfWeek, _) -> DayOfWeek.valueOf(dayOfWeek.uppercase()) }
            .mapValues { (_, dailyHours) -> dailyHours.map(OpeningPeriodDTO::toOpeningPeriod) })
}

data class OpeningPeriodDTO(
    @JsonDeserialize(using = LocalTimeDeserializer::class) val open: LocalTime,
    @JsonDeserialize(using = LocalTimeDeserializer::class) val close: LocalTime
) {
    fun toOpeningPeriod(): OpeningPeriod = OpeningPeriod(open, close)
}