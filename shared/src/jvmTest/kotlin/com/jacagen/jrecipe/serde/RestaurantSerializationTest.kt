package com.jacagen.jrecipe.serde

// For demonstration only; consider moving to a test source set or directory as appropriate.
import com.fasterxml.jackson.module.kotlin.readValue
import com.jacagen.jrecipe.llm.serde.objectMapper
import com.jacagen.jrecipe.model.DayOfWeek
import com.jacagen.jrecipe.model.EntitySource
import com.jacagen.jrecipe.model.Restaurant
import kotlinx.serialization.json.Json
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class RestaurantSerializationTest {
    private fun deserialize(json: String) = objectMapper.readValue<Restaurant>(json)

    @Test
    fun testRestaurantDeserialization() {
        val json = """
            {
                "id": "test123",
                "name": "Testaurant",
                "address": "123 Main St",
                "open": {
                    "hours": {
                        "MONDAY": [
                            { "open": "11:00", "close": "21:00" }
                        ]
                    }
                },
                "tags": ["test", "food"],
                "source": "EVERNOTE"
            }
        """.trimIndent()

        val restaurant = deserialize(json)

        assertEquals("test123", restaurant.id)
        assertEquals("Testaurant", restaurant.name)
        assertEquals("123 Main St", restaurant.address)
        assertEquals(LocalTime.parse("11:00"), restaurant.open.hours[DayOfWeek.MONDAY]?.first()?.open)
        assertEquals(LocalTime.parse("21:00"), restaurant.open.hours[DayOfWeek.MONDAY]?.first()?.close)
        assertEquals(setOf("test", "food"), restaurant.tags)
        assertEquals(EntitySource.EVERNOTE, restaurant.source)
    }

    @Test
    fun testRestaurantDeserializationMixedCaseDayOfWeek() {
        val json = """
            {
                "id": "test123",
                "name": "Testaurant",
                "address": "123 Main St",
                "open": {
                    "hours": {
                        "Monday": [
                            { "open": "11:00", "close": "21:00" }
                        ]
                    }
                },
                "tags": ["test", "food"],
                "source": "EVERNOTE"
            }
        """.trimIndent()

        val restaurant = Json.decodeFromString<Restaurant>(json)

        assertEquals("test123", restaurant.id)
        assertEquals("Testaurant", restaurant.name)
        assertEquals("123 Main St", restaurant.address)
        assertEquals(LocalTime.parse("11:00"), restaurant.open.hours[DayOfWeek.MONDAY]?.first()?.open)
        assertEquals(LocalTime.parse("21:00"), restaurant.open.hours[DayOfWeek.MONDAY]?.first()?.close)
        assertEquals(setOf("test", "food"), restaurant.tags)
        assertEquals(EntitySource.EVERNOTE, restaurant.source)
    }

    @Test
    fun testRestaurantDeserializationOnlyOneSetOfHoursForDayOfWeek() {
        val json = """
            {
                "id": "bred_kendall_sq",
                "name": "Bred Gourmet",
                "address": "730 Main Street, Cambridge, MA 02139",
                "open": {
                    "hours": {
                        "Monday": { "open": "11:00", "close": "21:00" },
                        "Tuesday": { "open": "11:00", "close": "21:00" },
                        "Wednesday": { "open": "11:00", "close": "21:00" },
                        "Thursday": { "open": "11:00", "close": "21:00" },
                        "Friday": { "open": "11:00", "close": "23:00" },
                        "Saturday": { "open": "11:00", "close": "23:00" }
                    }
                },
                "tags": ["food", "food-burger", "restaurant", "loc-area-four"],
                "source": "APPLE_NOTE"
            }
        """.trimIndent()

        val restaurant = Json.decodeFromString<Restaurant>(json)

        assertEquals("bred_kendall_sq", restaurant.id)
        assertEquals("Bred Gourmet", restaurant.name)
        assertEquals("730 Main Street, Cambridge, MA 02139", restaurant.address)
        assertEquals(LocalTime.parse("11:00"), restaurant.open.hours[DayOfWeek.MONDAY]?.first()?.open)
        assertEquals(LocalTime.parse("21:00"), restaurant.open.hours[DayOfWeek.MONDAY]?.first()?.close)
        assertEquals(setOf("food", "food-burger", "restaurant", "loc-area-four"), restaurant.tags)
        assertEquals(EntitySource.APPLE_NOTE, restaurant.source)
    }

    @Test
    fun anotherTest() {
        val json = """
            {
                "id": "6f713c9a-bbfb-4f90-981f-9e7834294f6f",
                "name": "BRED GOURMET",
                "address": "730 Main Street, Cambridge, MA 02139",
                "open": {
                  "hours": {
                    "Monday": ["11:00", "21:00"],
                    "Tuesday": ["11:00", "21:00"],
                    "Wednesday": ["11:00", "21:00"],
                    "Thursday": ["11:00", "21:00"],
                    "Friday": ["11:00", "23:00"],
                    "Saturday": ["11:00", "23:00"],
                    "Sunday": []
                  }
                },
                "tags": ["food", "food-burger", "restaurant", "loc-area-four"],
                "source": "APPLE_NOTE"
            }
        """.trimIndent()

        val restaurant = Json.decodeFromString<Restaurant>(json)

        assertEquals("6f713c9a-bbfb-4f90-981f-9e7834294f6f", restaurant.id)
        assertEquals("BRED GOURMET", restaurant.name)
        assertEquals("730 Main Street, Cambridge, MA 02139", restaurant.address)
        assertEquals(LocalTime.parse("11:00"), restaurant.open.hours[DayOfWeek.MONDAY]?.first()?.open)
        assertEquals(LocalTime.parse("21:00"), restaurant.open.hours[DayOfWeek.MONDAY]?.first()?.close)
        assertEquals(setOf("food", "food-burger", "restaurant", "loc-area-four"), restaurant.tags)
        assertEquals(EntitySource.APPLE_NOTE, restaurant.source)
    }


}