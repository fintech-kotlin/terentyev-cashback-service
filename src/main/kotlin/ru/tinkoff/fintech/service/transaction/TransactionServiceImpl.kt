package ru.tinkoff.fintech.service.transaction

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.client.CardServiceClient
import ru.tinkoff.fintech.client.ClientService
import ru.tinkoff.fintech.client.LoyaltyServiceClient
import ru.tinkoff.fintech.db.entity.LoyaltyPaymentEntity
import ru.tinkoff.fintech.db.repository.LoyaltyPaymentRepository
import ru.tinkoff.fintech.model.*
import ru.tinkoff.fintech.service.cashback.CashbackCalculator
import ru.tinkoff.fintech.service.notification.NotificationService
import java.time.LocalDate


@Service
class TransactionServiceImpl(
    private val cardServiceClient: CardServiceClient,
    private val clientService: ClientService,
    private val loyaltyServiceClient: LoyaltyServiceClient,
    private val notificationService: NotificationService,
    private val cashbackCalculator: CashbackCalculator,
    private val loyaltyPaymentRepository: LoyaltyPaymentRepository,
    @Value("\${payment.sign}")
    private val serviceSign: String
) : TransactionService {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    override fun handle(transaction: Transaction) {
        if (transaction.mccCode == null) {
            log.info { "mccCode is NULL: $transaction" }
            return
        }
        try {
            val card = cardServiceClient.getCard(transaction.cardNumber)
            val client = clientService.getClient(card.client)
            val loyaltyProgram = loyaltyServiceClient.getLoyaltyProgram(card.loyaltyProgram)

            val firstMonthDay = LocalDate.from(transaction.time).withDayOfMonth(1).atStartOfDay()
            val payments = loyaltyPaymentRepository.findAllByCardIdAndSignAndDateTimeAfter(card.id, serviceSign, firstMonthDay)

            val monthSum = payments.fold(0.0, { acc, entity -> acc + entity.value } )

            val transactionInfo = createTransactionInfo(loyaltyProgram, transaction, client, monthSum)
            val cashback = cashbackCalculator.calculateCashback(transactionInfo)

            saveLoyaltyPayment(card, cashback, transaction)

            val message = createNotificationMessage(transaction, transactionInfo, cashback)
            notificationService.sendNotification(client.id, message)

        } catch (e : Exception) {
            log.error("Call service error ", e)
        }
    }

    private fun createTransactionInfo(loyaltyProgram: LoyaltyProgram, transaction: Transaction, client: Client, monthSum: Double) =
    TransactionInfo(
        loyaltyProgram.name,
        transaction.value,
        monthSum,
        transaction.mccCode,
        client.birthDate?.toLocalDate(),
        client.firstName!!,
        client.lastName!!,
        client.middleName!!
    )

    private fun createNotificationMessage(transaction: Transaction, transactionInfo: TransactionInfo, cashback: Double) =
        NotificationMessageInfo(
            transactionInfo.firstName,
            transaction.cardNumber,
            cashback,
            transactionInfo.transactionSum,
            transactionInfo.loyaltyProgramName,
            transaction.time
        )

    private fun saveLoyaltyPayment(card: Card, cashback: Double, transaction: Transaction) {
        val loyaltyPayment = LoyaltyPaymentEntity(
            value = cashback,
            cardId = card.id,
            sign = serviceSign,
            transactionId = transaction.transactionId,
            dateTime = transaction.time
        )
        loyaltyPaymentRepository.save(loyaltyPayment)
    }
}