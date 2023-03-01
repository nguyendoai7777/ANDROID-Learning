package com.example.forlearning.ui.Search

data class Person(
    val fName: String,
    val lName: String
) {
    fun match(text: String): Boolean {
        val matchingCombination = listOf(
            "$fName$lName",
            "$fName $lName",
            "${fName.first()} ${lName.first()}",
        )
        return matchingCombination.any {
            it.contains(text, ignoreCase = true)
        }
    }
}

val personList = listOf(
    Person("Doai", "Nguyen"),
    Person("Brendan", "Eich"),
    Person("Lars", "Bak"),
    Person("Minko", "Gechev"),
    Person("Apple", "Tree"),
    Person("Google", "Ball"),
)