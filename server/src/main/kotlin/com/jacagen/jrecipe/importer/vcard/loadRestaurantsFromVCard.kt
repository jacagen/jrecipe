@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.importer.vcard

import com.jacagen.jrecipe.model.Restaurant
import com.jacagen.jrecipe.model.RestaurantId
import ezvcard.VCard
import ezvcard.io.text.VCardReader
import io.ktor.server.util.url
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun loadRestaurantsFromVCard() {
    getCards()
//        .map {
//            it.also { card ->
//                println(it)
//                println("---")
//                println("Notes")
//                card.notes.forEach { note ->
//                    println("> ${note.value}")
//                }
//                println("---")
//            }
//        }
//        .map { it.toRestaurant() }
        .map {
            it.also {
                println(it)
                println("************************")
            }
        }
        .toList()
}

private fun getCards() = sequence {
    val stream = object {}::class.java.getClassLoader().getResourceAsStream("restaurants/Restaurants.vcf")!!
    val reader = VCardReader(stream)
    reader.use { reader ->
        var vcard = reader.readNext()
        while (vcard != null) {
            yield(vcard)
            vcard = reader.readNext()
        }
    }
}

private fun VCard.assertIsCompany() {
    if (getExtendedProperty("X-ABShowAs").value != "COMPANY")
        throw Exception("This card is not a company")
}

private fun VCard.getOrg() = organization

private fun VCard.toRestaurant(): Restaurant {
    val url = when {
        urls == null || urls.size == 0 -> null
        urls.size == 1 -> urls[0].value
        urls.size > 0 -> throw IllegalArgumentException("Too many urls")
        else -> throw IllegalStateException()
    }
    return Restaurant(
        id = Uuid.random().toString(),
        name = formattedName.value,
        url = url,
    )
}


//1.	ezvcard.property.ProductId
//2.	ezvcard.property.StructuredName
//3.	ezvcard.property.FormattedName
//4.	ezvcard.property.Organization
//5.	ezvcard.property.Note
//6.	ezvcard.property.RawProperty
//7.	ezvcard.property.Url
//8.	ezvcard.property.Telephone
//9.	ezvcard.property.Address
//10.	ezvcard.property.Email