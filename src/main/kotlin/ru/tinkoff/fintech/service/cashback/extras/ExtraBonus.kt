package ru.tinkoff.fintech.service.cashback.extras

import ru.tinkoff.fintech.model.TransactionInfo
import java.math.BigDecimal

interface ExtraBonus {
    fun calculate(transactionInfo: TransactionInfo) : BigDecimal
}