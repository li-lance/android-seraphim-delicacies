package com.seraphim.delicacies.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlin.text.get

expect fun getPlatformEngine(): HttpClientEngineFactory<*>
expect fun HttpClientConfig<*>.platformEngineConfig()


object HttpClientFactory {
    fun create(
        baseUrl: String,
        headers: Map<String, String> = emptyMap(),
        timeoutMillis: Long = 30_000,
        json: Json = com.seraphim.delicacies.shared.serializable.json,
        logLevel: LogLevel = LogLevel.INFO,
        ktorLogger: Logger = Logger.DEFAULT,
    ): HttpClient {
        return HttpClient(getPlatformEngine()) {
            install(ContentNegotiation) { json(json) }
            // 超时
            install(HttpTimeout) {
                requestTimeoutMillis = timeoutMillis
                connectTimeoutMillis = timeoutMillis
                socketTimeoutMillis = timeoutMillis
            }
            install(DefaultRequest) {
                url(baseUrl)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                headers.forEach { (k, v) -> header(k, v) }
            }
            install(Logging) {
                level = logLevel
                logger = ktorLogger
            }
            HttpResponseValidator {
                validateResponse { response ->
                    if (!response.status.isSuccess()) {
                        throw ResponseException(response, response.bodyAsText())
                    }
                }
                handleResponseExceptionWithRequest { cause, _ ->
                    throw asNetworkException(cause)
                }
            }
            // 平台相关引擎细节（Android 配置 OkHttp，iOS 配置 Darwin）
            platformEngineConfig()
        }
    }
}

// 统一错误映射（提供给 HttpResponseValidator 和外部 runCatching 使用）
suspend fun asNetworkException(t: Throwable): Throwable = when (t) {
    is ResponseException -> NetworkException.Http(
        status = t.response.status.value,
        body = runCatching { t.response.bodyAsText() }.getOrNull(),
        cause = t
    )

    is HttpRequestTimeoutException, is SocketTimeoutException -> NetworkException.Timeout(t)
    is IOException -> NetworkException.Network(t)
    is kotlinx.serialization.SerializationException -> NetworkException.Serialization(t)
    else -> NetworkException.Unknown(t)
}

// 简化安全调用模板
suspend inline fun <reified T> HttpClient.safeGet(
    path: String,
    noinline builder: HttpRequestBuilder.() -> Unit = {}
): Result<T> = runCatching { this.get(path, builder).body<T>() }
    .fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(asNetworkException(it)) }
    )
//// 给 Result 增加错误映射
//fun <T> Result<T>.mapFailure(transform: (Throwable) -> Throwable): Result<T> =
//    fold(
//        onSuccess = { Result.success(it) },
//        onFailure = { Result.failure(transform(it)) }
//    )
