package com.seraphim.delicacies.shared.network

import com.seraphim.delicacies.shared.model.ImageDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get

interface ApiService {
    suspend fun getImage(): BffResult<ImageDto>
}

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {
    override suspend fun getImage(): BffResult<ImageDto> = receiveBffResult {
        client.get("https://foodish-api.com/api/") {
            //do nothing
        }
    }
}