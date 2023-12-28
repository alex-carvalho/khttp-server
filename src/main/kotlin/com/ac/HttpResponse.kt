package com.ac

import java.io.BufferedWriter
import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
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

    fun writeTo(channel: WritableByteChannel) {
        writeResponse(channel, this)
    }
}


private fun writeResponse(channel: WritableByteChannel, response: HttpResponse) {
    try {
        val breadLine = "\r\n"

        channel.write(("HTTP/1.1 ${response.status.code} ${response.status.statusMessage}  $breadLine").toByteBuffer())
        writeHeaders(channel, response.responseHeaders)

        val entityString = response.entity?.toString();

        if (entityString.isNullOrEmpty()) {
            channel.write(breadLine.toByteBuffer())
        } else {
            channel.write(("Content-Length: " + entityString.length + breadLine).toByteBuffer())
            channel.write(breadLine.toByteBuffer())
            channel.write(entityString.toByteBuffer())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun writeHeaders(channel: WritableByteChannel, responseHeaders: Map<String, List<String?>>) {
    responseHeaders.forEach { (name: String, values: List<String?>) ->
        val valuesCombined = StringJoiner(";")
        values.forEach{ str: String? -> valuesCombined.add(str) }
        channel.write("$name: $valuesCombined\r\n".toByteBuffer())
    }
}

fun String.toByteBuffer(): ByteBuffer = ByteBuffer.wrap(this.encodeToByteArray())


private fun writeResponse(outputStream: BufferedWriter, response: HttpResponse) {
    try {
        val breadLine = "\r\n"

        outputStream.write("HTTP/1.1 ${response.status.code} ${response.status.statusMessage}  $breadLine")
        writeHeaders(outputStream, response.responseHeaders)

        val entityString = response.entity?.toString();

        if (entityString.isNullOrEmpty()) {
            outputStream.write(breadLine)
        } else {
            outputStream.write("Content-Length: " + entityString.length + breadLine)
            outputStream.write(breadLine)
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

