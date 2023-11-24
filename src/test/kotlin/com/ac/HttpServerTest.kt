package com.ac

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HttpServerTest {

    @Test
    fun testHttpServerOK() {
        val server = HttpServer(8080)
            .addRoute(HttpMethod.GET, "/") {
                HttpResponse.ok("works")
            }

        server.start()

        val response = khttp.get("http://localhost:8080")

        assertEquals(200, response.statusCode)
        assertEquals("works", response.text)
        assertEquals("KotlinServer", response.headers["Sever"])
        assertEquals("5", response.headers["Content-Length"])
        assertNotNull(response.headers["Date"])

        server.stop()
    }

    @Test
    fun testHttpServerNotFound() {
        val server = HttpServer(8080)
        server.start()

        val response = khttp.get("http://localhost:8080")

        assertEquals(404, response.statusCode)
        assertEquals("Route Not Found...", response.text)
        assertEquals("KotlinServer", response.headers["Sever"])
        assertEquals("18", response.headers["Content-Length"])
        assertNotNull(response.headers["Date"])

        server.stop()
    }
}