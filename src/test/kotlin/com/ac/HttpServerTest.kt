package com.ac

import com.ac.io.HttpMethod
import com.ac.io.KHttpServer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HttpServerTest {

    @Test
    fun testHttpServerOK() {
        val server = KHttpServer(8080)
            .addRoute(HttpMethod.GET, "/") {
                HttpResponse.ok("works")
            }

        server.start()

        val response = khttp.get("http://localhost:8080")

        assertEquals(200, response.statusCode)
        assertEquals("works", response.text)
        assertEquals("KotlinServer", response.headers["Server"])
        assertEquals("5", response.headers["Content-Length"])
        assertNotNull(response.headers["Date"])

        server.stop()
    }

    @Test
    fun testHttpServerNotFound() {
        val server = KHttpServer(8080)
        server.start()

        val response = khttp.get("http://localhost:8080")

        assertEquals(404, response.statusCode)
        assertEquals("Route Not Found...", response.text)
        assertEquals("KotlinServer", response.headers["Server"])
        assertEquals("18", response.headers["Content-Length"])
        assertNotNull(response.headers["Date"])

        server.stop()
    }
}