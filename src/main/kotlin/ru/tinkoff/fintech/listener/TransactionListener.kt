package ru.tinkoff.fintech.listener

import mu.KLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.tinkoff.fintech.model.Transaction
import ru.tinkoff.fintech.service.transaction.TransactionService

@Component
class TransactionListener(
    private val transactionService : TransactionService
) {
    companion object : KLogging()

    @KafkaListener(topics = ["\${spring.kafka.consumer.topic}"])
    fun onMessage(transaction: Transaction) {
        try {
            transactionService.handle(transaction)
        } catch (e: Exception) {
            logger.error("Get error when try handle transaction($transaction) {}", e)
        }
    }
}


