package com.ac.io.handler

import com.ac.io.HttpRequestHandler
import java.io.IOException
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ThreadPoolHttpServerWorker(
    private val executor: ExecutorService = Executors.newFixedThreadPool(100)) : HttpServerWorker {

    override fun stop() {
        executor.shutdownNow()
    }

    override fun handleConnection(socket: Socket, httpRequestHandler: HttpRequestHandler) {
        val httpRequestRunner = Runnable {
            try {
                httpRequestHandler.handleConnection(socket)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        executor.execute(httpRequestRunner)
    }
}