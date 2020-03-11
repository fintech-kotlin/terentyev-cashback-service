package ru.tinkoff.fintech.service.cashback

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.model.TransactionInfo
import ru.tinkoff.fintech.service.cashback.rules.*
import ru.tinkoff.fintech.service.cashback.extras.*
import java.math.BigDecimal
import java.math.RoundingMode

internal const val LOYALTY_PROGRAM_BLACK = "BLACK"
internal const val LOYALTY_PROGRAM_ALL = "ALL"
internal const val LOYALTY_PROGRAM_BEER = "BEER"
internal const val MAX_CASH_BACK = 3000.0
internal const val MCC_SOFTWARE = 5734
internal const val MCC_BEER = 5921

@Service
class CashbackCalculatorImpl : CashbackCalculator {
    companion object {
        val bonusList = listOf<ExtraBonus>(TripleSixExtraBonusImpl())
    }

    override fun calculateCashback(transactionInfo: TransactionInfo): Double {
        var cashback = calculateCashbackBody(transactionInfo)
        cashback += calculateCashbackBonus(transactionInfo)
        cashback = trimToMaxCashbackIfMore(cashback, BigDecimal(transactionInfo.cashbackTotalValue))

        return cashback.setScale(2, RoundingMode.CEILING).toDouble()
    }

    private fun calculateCashbackBody(info: TransactionInfo): BigDecimal =
        when (info.loyaltyProgramName) {
            LOYALTY_PROGRAM_BLACK -> BlackProgramRuleImpl().calculate(info)
            LOYALTY_PROGRAM_ALL   -> AllProgramRuleImpl().calculate(info)
            LOYALTY_PROGRAM_BEER  -> BeerProgramRuleImpl().calculate(info)
            else                  -> BigDecimal.ZERO
        }

    private fun calculateCashbackBonus(transactionInfo: TransactionInfo): BigDecimal =
        bonusList.fold(BigDecimal.ZERO, { acc, bonus -> acc + bonus.calculate(transactionInfo)})

    private fun trimToMaxCashbackIfMore(cashback: BigDecimal, cashbackTotalValue: BigDecimal): BigDecimal {
        val maxCashback = BigDecimal(MAX_CASH_BACK)
        return if ((cashbackTotalValue + cashback) > maxCashback) maxCashback - cashbackTotalValue else cashback
    }
}