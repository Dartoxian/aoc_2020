package aoc_21
import java.io.File

data class Ingredient(val name: String, val allergen: String? = null) {
    fun withAllergen(allergen: String): Ingredient = if (this.allergen != null)
        Ingredient(name, allergen)
        else throw IllegalAccessException("Cannot set an additional allergen on an ingredient with a known allergen")
}

class Food(val ingredients: Collection<Ingredient>, val allergens: Collection<String>) {
    companion object {
        fun parse(s: String): Food {
            val (rawInredients, rawAllergens) = s.split(" (contains ")
            val ingredients = rawInredients.split(" ").map { Ingredient(it) }
            val allergens = rawAllergens.split(", ").map { it.replace(")", "") }
            return Food(ingredients, allergens)
        }
    }

    override fun toString(): String {
        return "Food($ingredients, $allergens)"
    }
}

fun readInput(): List<Food> = File(ClassLoader.getSystemResource("aoc_21_input.txt").file)
    .readLines()
    .map(Food::parse)

fun main() {
    val foods = readInput()
    val allergens = foods.flatMap { it.allergens }.toSet()

    var allergensToPossibleIngredients = allergens.associateWith { allergen ->
        foods.filter { food -> allergen in food.allergens }
            .map { food -> food.ingredients }
            .reduce{ a,b -> a.intersect(b)}
    }

    val knownAllergens = mutableMapOf<Ingredient, String>()
    while (allergensToPossibleIngredients.values.any { it.size == 1 }) {
        knownAllergens.putAll(allergensToPossibleIngredients.filterValues { it.size == 1 }.entries.associate { (k, v) -> v.first() to k })
        allergensToPossibleIngredients = allergensToPossibleIngredients
            .filterValues { it.size >= 1 }
            .mapValues { (_, ingredients) -> ingredients.subtract(knownAllergens.keys) }
    }
    println(allergensToPossibleIngredients)
    println(knownAllergens)

    val ingredientsWithPossibleAllergens = knownAllergens.keys + allergensToPossibleIngredients.values.flatten().toSet()


    val allergyFreeIngredients = foods.flatMap { it.ingredients }
        .filter { it !in ingredientsWithPossibleAllergens }.map { it.name }
    println("These ingredients do not contain allergens: ${allergyFreeIngredients.toSet()}")
    println("They appear ${allergyFreeIngredients.size} times")

    val dangerousIngredientList = knownAllergens.entries
        .sortedBy { (_, allergen) -> allergen }
        .map { (ingredient, _) -> ingredient.name}
        .joinToString(",")
    println("The canonical dangerous ingredient list is: $dangerousIngredientList")
}