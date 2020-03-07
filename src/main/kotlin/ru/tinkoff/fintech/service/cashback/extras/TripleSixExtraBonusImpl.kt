package ru.tinkoff.fintech.service.cashback.extras

import ru.tinkoff.fintech.model.TransactionInfo
import java.math.BigDecimal

class TripleSixExtraBonusImpl : ExtraBonus {
    companion object {
        private const val TRIPLE_SIX = 666.0
    }
    override fun calculate(transactionInfo: TransactionInfo): BigDecimal = with(transactionInfo) {
        var cashback = BigDecimal.ZERO

        if (transactionSum == TRIPLE_SIX || transactionSum % TRIPLE_SIX == 0.0) {
            cashback += BigDecimal(TRIPLE_SIX).divide(BigDecimal(100))
        }

        return cashback
    }

}