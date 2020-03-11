package ru.tinkoff.fintech.service.transaction

import ru.tinkoff.fintech.model.Transaction

interface TransactionService {
    fun handle(transaction: Transaction)
}