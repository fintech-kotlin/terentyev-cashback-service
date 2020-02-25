package ru.tinkoff.fintech.service.notification

class CardNumberMaskerImpl: CardNumberMasker {

    override fun mask(cardNumber: String, maskChar: Char, start: Int, end: Int): String {
        if (start > end) throw Exception("Start index cannot be greater than end index")
        if (cardNumber.isBlank() || start == end) return cardNumber
        val endIndex = if (end > cardNumber.length) cardNumber.length else end
        return cardNumber.replaceRange(start, endIndex, maskChar.toString().repeat(endIndex - start))
    }
}