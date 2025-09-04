package com.seraphim.delicacies.shared.network

sealed class NetworkException(message: String?, cause: Throwable? = null) : Exception(message, cause) {
    class Timeout(cause: Throwable? = null) : NetworkException("Request timed out", cause)
    class Network(cause: Throwable? = null) : NetworkException("Network I/O error", cause)
    class Serialization(cause: Throwable? = null) : NetworkException("Serialization error", cause)
    class Http(val status: Int, val body: String? = null, cause: Throwable? = null) :
        NetworkException("HTTP error $status${body?.let { ": $it" } ?: ""}", cause)

    class Unknown(cause: Throwable? = null) : NetworkException("Unknown error", cause)
}