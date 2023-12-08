package com.ac

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*


class LoadTest {

    @Test
    fun loadTest() {
        val server = KHttpServer(8080)
            .addRoute(HttpMethod.GET, "/" ){
                HttpResponse.ok(UUID.randomUUID().toString())
            }
        server.start()


        val file = File(javaClass.getClassLoader().getResource("load-test.js")?.file)
        val absolutePath = file.absolutePath

        val rt = Runtime.getRuntime()
        val commands = arrayOf("k6", "run", absolutePath)
        val process = rt.exec(commands)

        val stdInput = BufferedReader(InputStreamReader(process.inputStream))

        var s: String?
        while (stdInput.readLine().also { s = it } != null) {
            println(s)
        }

        print(String(process.errorStream.readAllBytes(), StandardCharsets.UTF_8))
    }
}