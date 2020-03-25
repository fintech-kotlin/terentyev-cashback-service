package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Client

@Component
class ClientServiceImpl(
    @Value("\${rest.api.client}")
    private val url: String,
    private val restClient: RestTemplate
) : ClientService {

    override fun getClient(id: String): Client {
        val res = restClient.getForEntity("$url/$id", Client::class.java)
        if (!res.statusCode.is2xxSuccessful) {
            throw RestClientException("Incorrect status: ${res.statusCodeValue}")
        }
        return res.body!!
    }

}