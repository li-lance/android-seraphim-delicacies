package com.seraphim.delicacies.shared.network

import com.seraphim.delicacies.shared.model.ImageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface ApiService {
    suspend fun getImage(): ImageDto
}
class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {
    override suspend fun getImage(): ImageDto  = client.get("https://foodish-api.com/api/").body()
}