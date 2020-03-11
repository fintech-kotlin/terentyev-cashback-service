package ru.tinkoff.fintech.service.notification

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.client.NotificationServiceClient
import ru.tinkoff.fintech.model.NotificationMessageInfo

@Service
class NotificationServiceImpl(
    private val notificationServiceClient: NotificationServiceClient,
    private val notificationMessageGenerator: NotificationMessageGenerator
) : NotificationService {

    override fun sendNotification(clientId: String, notificationMessageInfo: NotificationMessageInfo) {
        val message = notificationMessageGenerator.generateMessage(notificationMessageInfo)

        notificationServiceClient.sendNotification(clientId, message)
    }
}