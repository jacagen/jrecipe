package com.jacagen.jrecipe.model

val tagsDefinitions = listOf(
    TagDefinition("Side", "Course", setOf("#food-side")),
    TagDefinition("Main Course", "Course", setOf("#food-main-course")),
    TagDefinition("Snack", "Course"),
    TagDefinition("Hors D'ouevres", "Course"),
    TagDefinition("Appetizer", "Course"),
    TagDefinition("Drink", "Course"),
    TagDefinition("Meal", "Course", setOf("#food-meal")),

    TagDefinition("Easy", "Difficulty", setOf("Ian can prepare")),
    TagDefinition("Medium", "Difficulty", setOf("#food-recipe-medium-effort")),

    TagDefinition("Breakfast", "Meal"),
    TagDefinition("Lunch at School", "Meal"),
    TagDefinition("Dim Sum", "Meal", setOf("Димсам")),
    TagDefinition("Dessert", "Meal"),
    TagDefinition("Main Course", "Meal", setOf("#food-main-course")),

    TagDefinition("Mexican", "Cuisine", setOf("Мексиканский")),
    TagDefinition("Chinese", "Cuisine", setOf("Китайский")),
    TagDefinition("Italian", "Cuisine", setOf("Итальянский")),
    TagDefinition("Polish", "Cuisine", setOf("PolishCuisine")),
    TagDefinition("Cajun/Creole", "Cuisine", setOf("Каджунский/креольский")),
    TagDefinition("Korean", "Cuisine", setOf("Корейский")),
    TagDefinition("Japanese", "Cuisine", setOf("Японский")),
    TagDefinition("Hungarian", "Cuisine"),
    TagDefinition("Russian", "Cuisine", setOf("RussianCuisine")),
    TagDefinition("Szechuan", "Chinese", setOf("Сычыаньский")),

    TagDefinition("Grill", "Technique"),

    TagDefinition("Sauce"),
    TagDefinition("Vegetarian"),
    TagDefinition("Healthier"),
    TagDefinition("Sandwich", null, setOf("Сандвич")),
    TagDefinition("Bread", null, setOf("Сандвич")),
    TagDefinition("Vegetable"),
    TagDefinition("Soup", null, setOf("#food-soup")),
    TagDefinition("Seafood", null, setOf("Морепродукты")),
    TagDefinition("Salad"),
    TagDefinition("Comfort Food"),
    TagDefinition("Fruit"),
    TagDefinition("Burger", "Sandwich", setOf("Бургеры")),
    TagDefinition("Pasta"),
    TagDefinition("Fruit"),
    TagDefinition("Dumpling", null, setOf("Пельмени", "#food-dumpling")),
    TagDefinition("Taco", "Mexican", setOf("Тако")),
    TagDefinition("Pho", "Vietnamese", setOf("Фо")),
    TagDefinition("Pizza", "Italian", setOf("Пицца")),
    TagDefinition("Noodles", null, setOf("#food-noodles"))
)
