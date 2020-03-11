package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Card

@Component
class CardServiceClientImpl(
    @Value("\${rest.api.card}")
    private val url: String,
    private val restClient: RestTemplate
) : CardServiceClient {

    override fun getCard(id: String): Card {
        val res = restClient.getForEntity("$url/$id", Card::class.java)
        if (res.statusCode.is2xxSuccessful) {
            throw RestClientException("Incorrect status: ${res.statusCodeValue}")
        }
        return res.body!!
    }
}