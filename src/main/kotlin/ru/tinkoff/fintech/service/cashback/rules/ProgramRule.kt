package ru.tinkoff.fintech.service.cashback.rules

import ru.tinkoff.fintech.model.TransactionInfo
import java.math.BigDecimal

interface ProgramRule {
    fun addReward(cashback: BigDecimal, transactionSum: BigDecimal, percent: BigDecimal): BigDecimal =
        cashback + (transactionSum * percent).divide(BigDecimal(100))

    fun calculate(transactionInfo: TransactionInfo): BigDecimal
}