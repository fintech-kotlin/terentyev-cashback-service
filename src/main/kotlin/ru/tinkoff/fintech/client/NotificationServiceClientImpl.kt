package ru.tinkoff.fintech.client

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class NotificationServiceClientImpl(
    @Value("\${rest.api.notification}")
    private val url: String,
    private val restClient: RestTemplate
) : NotificationServiceClient {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    override fun sendNotification(clientId: String, message: String) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON_UTF8

        val res = restClient.postForEntity("$url/$clientId/message", HttpEntity(message, headers), String::class.java)
        if (!res.statusCode.is2xxSuccessful) {
            log.error("Unexpected status: {}", res.statusCodeValue)
        }
    }
}