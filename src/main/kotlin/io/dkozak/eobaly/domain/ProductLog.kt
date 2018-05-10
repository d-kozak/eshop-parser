package io.dkozak.eobaly.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class ProductLog(
        @Id
        @GeneratedValue
        val id: Long = 0,
        val url: String = "",
        val state: String = "",
        var alreadyChecked: Boolean = false

)