package com.ac
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.util.function.Function

typealias RequestRunner = Function<HttpRequest, HttpResponse>

class HttpHandler (private val routes: Map<String, RequestRunner>) {

    fun handleConnection(inputStream: InputStream, outputStream: OutputStream) {
        val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
        val request: HttpRequest = HttpRequest.decode(inputStream)
        handleRequest(request, bufferedWriter)
        bufferedWriter.close()
        inputStream.close()
    }

    private fun handleRequest(request: HttpRequest, bufferedWriter: BufferedWriter) {
        val routeKey: String = request.httpMethod.name.plus(request.uri.rawPath)
        if (routes.containsKey(routeKey)) {
            val httpResponse = routes[routeKey]!!.apply(request)
            httpResponse.writeTo(bufferedWriter)
        } else {
            HttpResponse(HttpStatusCode.NOT_FOUND, "Route Not Found...", mutableMapOf())
                .writeTo(bufferedWriter)
        }
    }
}

