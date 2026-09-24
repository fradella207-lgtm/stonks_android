package com.stonks.app.model

enum class TransactionCategory(val displayName: String, val iconName: String) {
    FOOD("Cibo & Spesa", "Restaurant"),
    TRANSPORT("Trasporti", "DirectionsCar"),
    HOUSING("Casa & Utenze", "Home"),
    ENTERTAINMENT("Svago & Hobby", "SportsEsports"),
    SHOPPING("Shopping", "ShoppingBag"),
    HEALTH("Salute", "MedicalServices"),
    SALARY("Stipendio", "Payments"),
    FREELANCE("Freelance", "LaptopMac"),
    INVESTMENTS("Investimenti", "TrendingUp"),
    OTHER("Altro", "Category");

    companion object {
        fun fromString(value: String): TransactionCategory {
            return entries.firstOrNull { 
                it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) 
            } ?: OTHER
        }
    }
}
