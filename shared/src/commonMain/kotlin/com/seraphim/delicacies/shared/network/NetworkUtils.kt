package com.seraphim.delicacies.shared.network

import com.seraphim.delicacies.shared.serializable.json
import io.ktor.client.call.DoubleReceiveException
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException

internal suspend inline fun <reified DTO> receiveBffResult(
    block: () -> HttpResponse
): BffResult<DTO> = runCatchingOrError {
    val response = block()
    return if (response.status.isSuccess()) {
        try {
            BffResult.Success(response.body())
        } catch (e: NoTransformationFoundException) {
            BffResult.Failure.SerializationError(e)
        } catch (e: DoubleReceiveException) {
            BffResult.Failure.NetworkError(e)
        }
    } else {
        try {
            val errorText = response.readBytes().decodeToString()
            BffResult.Failure.LocalizedError(json.decodeFromString(errorText))
        } catch (e: SerializationException) {
            BffResult.Failure.HttpError(response.status)
        }
    }
}
