package com.jacagen.jrecipe.model

import com.jacagen.jrecipe.serde.DayOfWeekSerializer
import com.jacagen.jrecipe.serde.WeeklyHoursSerializer
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

typealias RestaurantId = String

@Serializable
data class Restaurant(
    val id: RestaurantId,
    val name: String,
    val address: String,    // Make this more structured, someday
    val open: WeeklyHours,
    val tags: Set<Tag>,
    val source: EntitySource,
)

@Serializable(with = DayOfWeekSerializer::class)
enum class DayOfWeek {  // ChatGPT suggested I needed to roll this myself--I suspect this is not true
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

@Serializable
data class OpeningPeriod(
    val open: LocalTime,
    val close: LocalTime
)

typealias DailyHours = List<OpeningPeriod>

@Serializable(with = WeeklyHoursSerializer::class)
data class WeeklyHours(
    val hours: Map<DayOfWeek, DailyHours> = emptyMap()
)