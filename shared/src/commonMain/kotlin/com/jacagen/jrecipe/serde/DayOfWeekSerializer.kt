package com.jacagen.jrecipe.serde

import com.jacagen.jrecipe.model.DailyHours
import com.jacagen.jrecipe.model.DayOfWeek
import com.jacagen.jrecipe.model.OpeningPeriod
import com.jacagen.jrecipe.model.WeeklyHours
import kotlinx.datetime.LocalTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

object DayOfWeekSerializer : KSerializer<DayOfWeek> {
    override val descriptor = PrimitiveSerialDescriptor("Day of week", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DayOfWeek) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DayOfWeek =
        DayOfWeek.valueOf(decoder.decodeString().uppercase())

}

object WeeklyHoursSerializer : KSerializer<WeeklyHours> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("WeeklyHours") {
        element("hours", JsonObject.serializer().descriptor)
    }

    override fun deserialize(decoder: Decoder): WeeklyHours {
        val input = decoder as? JsonDecoder
            ?: throw SerializationException("Expected JsonDecoder")
        val jsonObject = input.decodeJsonElement().jsonObject
        val hoursJson = jsonObject["hours"]?.jsonObject ?: return WeeklyHours()

        val hours = mutableMapOf<DayOfWeek, DailyHours>()
        for ((dayStr, json) in hoursJson) {
            val day = DayOfWeek.valueOf(dayStr.uppercase())

            val periods = decodeHoursSpec(json, input)

            hours[day] = periods
        }

        return WeeklyHours(hours)
    }

    private fun decodeHoursSpec(
        json: JsonElement,
        input: JsonDecoder
    ): List<OpeningPeriod> {
        val periods = when (json) {
            is JsonObject ->    // like: `{ "open": "11:00", "close": "21:00" }`
                listOf(input.json.decodeFromJsonElement(OpeningPeriod.serializer(), json))

            is JsonArray -> {    // either `[ { "open": "11:00", "close": "21:00" } ]` or `["11:00", "21:00"]`
                if (json.isEmpty())
                    return emptyList()
                else if (json[0] is JsonObject)
                    json.map {
                        input.json.decodeFromJsonElement(OpeningPeriod.serializer(), it)
                    }
                else {
                    val open = LocalTime.parse(json[0].jsonPrimitive.content)
                    val close = LocalTime.parse(json[1].jsonPrimitive.content)
                    return listOf(OpeningPeriod(open, close))
                }
            }

            else -> emptyList()
        }
        return periods
    }

    override fun serialize(encoder: Encoder, value: WeeklyHours) {
        val output = encoder as? JsonEncoder
            ?: throw SerializationException("Expected JsonEncoder")

        val hoursJson = value.hours.mapKeys { it.key.name.lowercase().replaceFirstChar(Char::uppercaseChar) }
            .mapValues { (_, periods) ->
                JsonArray(periods.map {
                    output.json.encodeToJsonElement(OpeningPeriod.serializer(), it)
                })
            }

        val result = JsonObject(mapOf("hours" to JsonObject(hoursJson)))
        output.encodeJsonElement(result)
    }
}