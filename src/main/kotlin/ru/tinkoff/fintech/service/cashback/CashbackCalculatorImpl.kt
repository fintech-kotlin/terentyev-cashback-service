package ru.tinkoff.fintech.service.cashback

import ru.tinkoff.fintech.model.TransactionInfo
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

internal const val LOYALTY_PROGRAM_BLACK = "BLACK"
internal const val LOYALTY_PROGRAM_ALL = "ALL"
internal const val LOYALTY_PROGRAM_BEER = "BEER"
internal const val MAX_CASH_BACK = 3000.0
internal const val MCC_SOFTWARE = 5734
internal const val MCC_BEER = 5921


private const val PERCENT_BLACK_DEFAULT = 1

private const val PERCENT_BEER_FIRST_LAST_NAME = 10
private const val PERCENT_BEER_FIRSTNAME = 7
private const val PERCENT_BEER_MONTH_LETTER = 5
private const val PERCENT_BEER_PREV_NEXT_MONTH_LETTER = 3
private const val PERCENT_BEER_DEFAULT = 2

private const val BEER_FIRSTNAME = "олег"
private const val BEER_LASTNAME  = "олегов"

private const val EXTRA_BONUS_TRIPLE_SIX = 666.0


class CashbackCalculatorImpl : CashbackCalculator {

    private val addBonus: BigDecimal.(transactionSum: BigDecimal, percent: BigDecimal) -> BigDecimal =
        { transactionSum, percent -> this + (transactionSum * percent).divide(BigDecimal(100)) }

    override fun calculateCashback(transactionInfo: TransactionInfo): Double = with(transactionInfo) {
        var cashback = when (loyaltyProgramName) {
            LOYALTY_PROGRAM_BLACK -> loyaltyBlack(this)
            LOYALTY_PROGRAM_ALL   -> loyaltyAll(this)
            LOYALTY_PROGRAM_BEER  -> loyaltyBeer(this)
            else                  -> BigDecimal.ZERO
        }
        cashback += extraBonus(this)

        // checks if the total cashback has exceeded MAX_CASH_BACK
        if ((BigDecimal(cashbackTotalValue) + cashback) > BigDecimal(MAX_CASH_BACK)) {
            cashback = BigDecimal(MAX_CASH_BACK) - BigDecimal(cashbackTotalValue)
        }

        return cashback.setScale(2, RoundingMode.CEILING).toDouble()
    }

    private fun loyaltyBlack(transactionInfo: TransactionInfo): BigDecimal {
        val transactSum = BigDecimal.valueOf(transactionInfo.transactionSum)
        val cashback = BigDecimal.ZERO

        return cashback.addBonus(transactSum, BigDecimal(PERCENT_BLACK_DEFAULT))
    }

    private fun loyaltyAll(transactionInfo: TransactionInfo): BigDecimal = with(transactionInfo) {
        val transactSum = BigDecimal.valueOf(transactionSum)
        var cashback = BigDecimal.ZERO

        if (mccCode == MCC_SOFTWARE && isPalindrome(transactionSum)) {
            val lcm = lcm(firstName.length, lastName.length)
            cashback = cashback.addBonus(transactSum, BigDecimal(lcm).divide(BigDecimal(1000)))
        }

        return cashback
    }

    private fun loyaltyBeer(transactionInfo: TransactionInfo): BigDecimal = with(transactionInfo) {
        val transactSum = BigDecimal.valueOf(transactionInfo.transactionSum)
        val cashback = BigDecimal.ZERO

        if (mccCode != MCC_BEER) {
            return cashback
        }

        val lowerName = firstName.toLowerCase()

        val percent = when {
            lowerName == BEER_FIRSTNAME
                    && lastName.toLowerCase() == BEER_LASTNAME   -> BigDecimal(PERCENT_BEER_FIRST_LAST_NAME)
            lowerName == BEER_FIRSTNAME                          -> BigDecimal(PERCENT_BEER_FIRSTNAME)
            lowerName.startsWith(getMonthLetter())               -> BigDecimal(PERCENT_BEER_MONTH_LETTER)
            lowerName.startsWith(getMonthLetter(1L))
                    || lowerName.startsWith(getMonthLetter(-1L)) -> BigDecimal(PERCENT_BEER_PREV_NEXT_MONTH_LETTER)
            else                                                 -> BigDecimal(PERCENT_BEER_DEFAULT)
        }
        return cashback.addBonus(transactSum, percent)
    }

    private fun extraBonus (transactionInfo: TransactionInfo): BigDecimal = with(transactionInfo) {
        var cashback = BigDecimal.ZERO

        if (transactionSum == EXTRA_BONUS_TRIPLE_SIX || transactionSum % EXTRA_BONUS_TRIPLE_SIX == 0.0) {
            cashback += BigDecimal(EXTRA_BONUS_TRIPLE_SIX).divide(BigDecimal(100))
        }

        return cashback
    }

    private fun getMonthLetter(monthsToAdd: Long = 0L): String {
        return DateTimeFormatter.ofPattern("MMMMM")
            .withLocale(Locale("ru"))
            .format(LocalDate.now().plusMonths(monthsToAdd)).toLowerCase()
    }

    private fun isPalindrome(number: Double): Boolean {
        val sum = (number * 100).toInt().toString()
        val palindrome = sum
            .take(sum.length / 2)
            .foldIndexed(0, { i, acc, char -> if (char != sum[sum.length-1 - i]) acc + 1 else acc } )

        return palindrome <= 1
    }

    private fun lcm(a: Int, b: Int): Int {
        if (a == 0 || b == 0) {
            return 0
        }
        val absA: Int = abs(a)
        val absB: Int = abs(b)
        val absMax = max(absA, absB)
        val absMin = min(absA, absB)
        var lcm = absMax
        while (lcm % absMin != 0) {
            lcm += absMax
        }
        return lcm
    }
}