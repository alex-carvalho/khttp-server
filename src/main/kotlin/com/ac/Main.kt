package com.ac

import com.ac.handler.ThreadPoolHttpServerWorker
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*


fun  main() {
    val server = KHttpServer(8080, ThreadPoolHttpServerWorker())
        .addRoute(HttpMethod.GET, "/" ){
            HttpResponse.ok(UUID.randomUUID().toString())
        }

    server.start()
}