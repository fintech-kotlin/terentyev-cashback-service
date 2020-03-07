package ru.tinkoff.fintech.service.cashback.rules

import ru.tinkoff.fintech.model.TransactionInfo
import java.math.BigDecimal

class BlackProgramRuleImpl : ProgramRule {
    companion object {
        private const val PERCENT_BLACK_DEFAULT = 1
    }

    override fun calculate(transactionInfo: TransactionInfo): BigDecimal {
        val transactSum = BigDecimal.valueOf(transactionInfo.transactionSum)
        val cashback = BigDecimal.ZERO

        return addReward(cashback, transactSum, BigDecimal(PERCENT_BLACK_DEFAULT))
    }
}