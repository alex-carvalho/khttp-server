package com.ac

import com.ac.io.handler.ThreadPoolHttpServerWorker
import com.ac.io.HttpMethod
import com.ac.io.KHttpServer
import com.ac.nio.KHttpServerNIO

import java.util.*
import java.util.concurrent.Executors


fun main() {
    ioThreadPoolServer()
    ioVirtualThreadPool()
    nioServer()

}

fun ioThreadPoolServer() {
    val server = KHttpServer(8080, ThreadPoolHttpServerWorker())
        .addRoute(HttpMethod.GET, "/") {
            HttpResponse.ok(UUID.randomUUID().toString())
        }

    server.start()
}

fun ioVirtualThreadPool() {
    val server = KHttpServer(8081, ThreadPoolHttpServerWorker(Executors.newVirtualThreadPerTaskExecutor()))
        .addRoute(HttpMethod.GET, "/") {
            HttpResponse.ok(UUID.randomUUID().toString())
        }

    server.start()
}

fun nioServer() {
    val server = KHttpServerNIO(8082)
        .addRoute(HttpMethod.GET, "/") {
            HttpResponse.ok(UUID.randomUUID().toString())
        }
        .addRoute(HttpMethod.GET, "/hello") {
            HttpResponse.ok("hello")
        }

    server.start()
}