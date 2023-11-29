package com.ac

import java.io.BufferedWriter
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


data class HttpResponse (val status : HttpStatusCode, val entity: Any?, val responseHeaders: MutableMap<String, List<String>> = mutableMapOf()) {
    companion object {
        fun ok(entity: Any?) : HttpResponse = HttpResponse(HttpStatusCode.OK, entity)
    }

    init {
        responseHeaders["Date"] = listOf(DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)))
        responseHeaders["Server"] = listOf("KotlinServer")
    }

    fun writeTo(outputStream: BufferedWriter) {
        writeResponse(outputStream, this)
    }
}

private fun writeResponse(outputStream: BufferedWriter, response: HttpResponse) {
    try {
        outputStream.write("HTTP/1.1 ${response.status.code} ${response.status.statusMessage}  \r\n")
        writeHeaders(outputStream, response.responseHeaders)

        val entityString = response.entity?.toString();

        if (entityString.isNullOrEmpty()) {
            outputStream.write("\r\n")
        } else {
            outputStream.write("Content-Length: " + entityString.length + "\r\n")
            outputStream.write("\r\n")
            outputStream.write(entityString)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun writeHeaders(outputStream: BufferedWriter, responseHeaders: Map<String, List<String?>>) {
    responseHeaders.forEach { (name: String, values: List<String?>) ->
        val valuesCombined = StringJoiner(";")
        values.forEach{ str: String? -> valuesCombined.add(str) }
        outputStream.write("$name: $valuesCombined\r\n")
    }
}

