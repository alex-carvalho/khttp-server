package com.ac

import java.io.ByteArrayOutputStream
import java.io.CharArrayWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI


data class HttpRequest(val httpMethod: HttpMethod, val uri: URI, val requestHeaders: Map<String, List<String>>) {
    companion object {
        fun decode(inputStream: InputStream): HttpRequest {
            return buildRequest(readMessage(inputStream))
        }
    }
}

private fun buildRequest(message: List<String>): HttpRequest {
    if (message.isEmpty()) {
        throw RuntimeException("invalid request")
    }
    val firstLine = message[0]
    val httpInfo = firstLine.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    if (httpInfo.size != 3) {
        throw RuntimeException("httpInfo invalid")
    }
    val protocolVersion = httpInfo[2]
    return if (protocolVersion != "HTTP/1.1") {
        throw RuntimeException("protocolVersion not supported")
    } else {
        HttpRequest(HttpMethod.valueOf(httpInfo[0]), URI(httpInfo[1]), addRequestHeaders(message))
    }
}

private fun readMessage(inputStream: InputStream): List<String> {
    return try {
        if (inputStream.available() <= 0) {
            throw RuntimeException("empty")
        }
        val inBuffer = CharArray(inputStream.available())
        val inReader = InputStreamReader(inputStream)
        inReader.read(inBuffer)
        String(inBuffer).lines()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}

private fun addRequestHeaders(message: List<String>):  MutableMap<String, List<String>> {
    val requestHeaders: MutableMap<String, List<String>> = mutableMapOf()

    if (message.size > 1) {
        for (i in 1..< message.size) {
            val header = message[i]
            val colonIndex = header.indexOf(':')
            if (!(colonIndex > 0 && header.length > colonIndex + 1)) {
                break
            }
            val headerName = header.substring(0, colonIndex)
            val headerValue = header.substring(colonIndex + 1)
            requestHeaders[headerName] = headerValue.split(",")
        }
    }

    return requestHeaders
}