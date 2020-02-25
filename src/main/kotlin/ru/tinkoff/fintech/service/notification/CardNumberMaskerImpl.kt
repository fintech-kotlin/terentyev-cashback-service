package ru.tinkoff.fintech.service.notification

class CardNumberMaskerImpl: CardNumberMasker {

    override fun mask(cardNumber: String, maskChar: Char, start: Int, end: Int): String =
        when {
            start > end -> throw IndexOutOfBoundsException("Start index cannot be greater than end index")
            cardNumber.isBlank() || start == end -> cardNumber
            else -> {
                val endIndex = if (end > cardNumber.length) cardNumber.length else end
                cardNumber.replaceRange(start, endIndex, maskChar.toString().repeat(endIndex - start))
            }
        }
}