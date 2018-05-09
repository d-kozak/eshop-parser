package io.dkozak.eobaly.domain

import java.util.*

data class ProductDetailsView(
        val details: List<ProductDetailView>
)

data class ProductDetailView(
        val timestamp: Date,
        val prize: Double,
        val amount: Long
)

