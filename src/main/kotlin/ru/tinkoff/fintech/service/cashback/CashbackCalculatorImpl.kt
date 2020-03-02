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

class CashbackCalculatorImpl : CashbackCalculator {

    private val addBonus: BigDecimal.(transactionSum: BigDecimal, percent: BigDecimal) -> BigDecimal =
        { transactionSum, percent -> this + (transactionSum * percent).divide(BigDecimal(100)) }

    override fun calculateCashback(transactionInfo: TransactionInfo): Double {
        with(transactionInfo) {
            var cashback = when (loyaltyProgramName) {
                LOYALTY_PROGRAM_BLACK -> loyaltyBlack(this)
                LOYALTY_PROGRAM_ALL   -> loyaltyAll(this)
                LOYALTY_PROGRAM_BEER  -> loyaltyBeer(this)
                else                  -> BigDecimal.ZERO
            }
            cashback += extraBonus(this)

            // checks if the total cashback has exceeded 3000
            if ((BigDecimal(cashbackTotalValue) + cashback) > BigDecimal(3000)) {
                cashback = BigDecimal(3000) - BigDecimal(cashbackTotalValue)
            }

            return cashback.setScale(2, RoundingMode.CEILING).toDouble()
        }
    }

    private fun loyaltyBlack(transactionInfo: TransactionInfo): BigDecimal {
        val transactSum = BigDecimal.valueOf(transactionInfo.transactionSum)
        val cashback = BigDecimal.ZERO

        return cashback.addBonus(transactSum, BigDecimal.ONE)
    }

    private fun loyaltyAll(transactionInfo: TransactionInfo): BigDecimal {
        with(transactionInfo) {
            val transactSum = BigDecimal.valueOf(transactionSum)
            var cashback = BigDecimal.ZERO

            if (mccCode == MCC_SOFTWARE && isPalindrome(transactionSum)) {
                val lcm = lcm(firstName.length, lastName.length)
                cashback = cashback.addBonus(transactSum, BigDecimal(lcm).divide(BigDecimal(1000)))
            }

            return cashback
        }
    }

    private fun loyaltyBeer(transactionInfo: TransactionInfo): BigDecimal {
        with(transactionInfo) {
            val transactSum = BigDecimal.valueOf(transactionInfo.transactionSum)
            var cashback = BigDecimal.ZERO

            if (mccCode == MCC_BEER) {
                val lowerName = firstName.toLowerCase()

                val percent = when {
                    lowerName == "олег" && lastName.toLowerCase() == "олегов"         -> BigDecimal(10)
                    lowerName == "олег"                                               -> BigDecimal(7)
                    lowerName.startsWith(getMonthLetter())                            -> BigDecimal(5)
                    lowerName.startsWith(getMonthLetter(1L))
                            || lowerName.startsWith(getMonthLetter(-1L)) -> BigDecimal(3)
                    else                                                              -> BigDecimal(2)
                }
                cashback = cashback.addBonus(transactSum, percent)
            }

            return cashback
        }

    }

    private fun extraBonus (transactionInfo: TransactionInfo): BigDecimal {
        var cashback = BigDecimal.ZERO
        with(transactionInfo) {
            if (transactionSum == 666.0 || transactionSum % 666 == 0.0) {
                cashback += BigDecimal(666).divide(BigDecimal(100))
            }
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