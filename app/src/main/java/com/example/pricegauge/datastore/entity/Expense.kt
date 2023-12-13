package com.example.pricegauge.datastore.entity

data class Expense(
    val id: Long,
    val amount: Double,
    val title: String,
    val note: String,
    val date: String,
    val createdAt: String,
    val updatedAt: String
)
