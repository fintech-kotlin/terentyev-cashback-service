package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.LoyaltyProgram

@Component
class LoyaltyServiceClientImpl(
    @Value("\${rest.api.loyalty}")
    private val url: String,
    private val restClient: RestTemplate
) : LoyaltyServiceClient {

    override fun getLoyaltyProgram(id: String): LoyaltyProgram {
        val res = restClient.getForEntity("$url/$id", LoyaltyProgram::class.java)
        if (!res.statusCode.is2xxSuccessful) {
            throw RestClientException("Incorrect status: ${res.statusCodeValue}")
        }
        return res.body!!
    }
}