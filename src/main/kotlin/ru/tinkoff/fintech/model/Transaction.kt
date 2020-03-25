package ru.tinkoff.fintech.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import ru.tinkoff.fintech.annotation.NoArg
import java.time.LocalDateTime

@NoArg
data class Transaction(
    val transactionId: String,
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val time: LocalDateTime,
    val cardNumber: String,
    val operationType: Int,
    val value: Double,
    val currencyCode: String,
    val mccCode: Int?
)
