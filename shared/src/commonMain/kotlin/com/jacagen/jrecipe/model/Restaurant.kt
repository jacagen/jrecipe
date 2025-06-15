package com.jacagen.jrecipe.model

import kotlinx.datetime.LocalTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias RestaurantId = String

data class Restaurant(
    val id: RestaurantId,
    val name: String,
    val address: String,    // Make this more structured, someday
    val open: WeeklyHours,
    val tags: Set<Tag>,
    val source: EntitySource,
)

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

data class OpeningPeriod(
    val open: LocalTime,
    val close: LocalTime
)

typealias DailyHours = List<OpeningPeriod>

data class WeeklyHours(
    val hours: Map<DayOfWeek, DailyHours> = emptyMap()
)