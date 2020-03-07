package ru.tinkoff.fintech.service.cashback.rules;

import ru.tinkoff.fintech.model.TransactionInfo
import ru.tinkoff.fintech.service.cashback.MCC_SOFTWARE
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class AllProgramRuleImpl : ProgramRule {
    companion object {
        private fun isPalindrome(number: Double): Boolean {
            val sum = (number * 100).toInt().toString()
            val palindrome = sum
                .take(sum.length / 2)
                .foldIndexed(0, { i, acc, char -> if (char != sum[sum.length-1 - i]) acc + 1 else acc } )

            return palindrome <= 1
        }

        private fun leastCommonMultiple(a: Int, b: Int): Int {
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

    override fun calculate(transactionInfo: TransactionInfo): BigDecimal = with(transactionInfo) {
        val transactSum = BigDecimal.valueOf(transactionSum)
        var cashback = BigDecimal.ZERO

        if (mccCode == MCC_SOFTWARE && isPalindrome(transactionSum)) {
            val lcm = leastCommonMultiple(firstName.length, lastName.length)
            cashback = addReward(cashback, transactSum, BigDecimal(lcm).divide(BigDecimal(1000)))
        }

        return cashback
    }

}
