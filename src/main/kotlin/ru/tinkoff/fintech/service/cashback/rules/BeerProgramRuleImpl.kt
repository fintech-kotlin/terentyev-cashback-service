package ru.tinkoff.fintech.service.cashback.rules

import ru.tinkoff.fintech.model.TransactionInfo
import ru.tinkoff.fintech.service.cashback.MCC_BEER
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class BeerProgramRuleImpl : ProgramRule {
    companion object {
        private const val PERCENT_BEER_FIRST_LAST_NAME = 10
        private const val PERCENT_BEER_FIRSTNAME = 7
        private const val PERCENT_BEER_MONTH_LETTER = 5
        private const val PERCENT_BEER_PREV_NEXT_MONTH_LETTER = 3
        private const val PERCENT_BEER_DEFAULT = 2

        private const val BEER_FIRSTNAME = "олег"
        private const val BEER_LASTNAME  = "олегов"

        private fun getMonthLetter(monthsToAdd: Long = 0L): String {
            return DateTimeFormatter.ofPattern("MMMMM")
                .withLocale(Locale("ru"))
                .format(LocalDate.now().plusMonths(monthsToAdd)).toLowerCase()
        }
    }

    override fun calculate(transactionInfo: TransactionInfo): BigDecimal = with(transactionInfo) {
        val transactSum = BigDecimal.valueOf(transactionInfo.transactionSum)
        val cashback = BigDecimal.ZERO

        if (mccCode != MCC_BEER) {
            return cashback
        }

        val lowerName = firstName.toLowerCase()

        val percent = when {
            lowerName == BEER_FIRSTNAME && lastName.toLowerCase() == BEER_LASTNAME -> PERCENT_BEER_FIRST_LAST_NAME
            lowerName == BEER_FIRSTNAME                                            -> PERCENT_BEER_FIRSTNAME
            lowerName.startsWith(getMonthLetter())                                 -> PERCENT_BEER_MONTH_LETTER
            lowerName.startsWith(getMonthLetter(1L))                               -> PERCENT_BEER_PREV_NEXT_MONTH_LETTER
            lowerName.startsWith(getMonthLetter(-1L))                              -> PERCENT_BEER_PREV_NEXT_MONTH_LETTER
            else                                                                   -> PERCENT_BEER_DEFAULT
        }

        return addReward(cashback, transactSum, BigDecimal(percent))
    }

}