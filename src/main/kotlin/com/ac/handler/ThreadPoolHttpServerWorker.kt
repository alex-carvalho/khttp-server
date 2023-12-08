package com.ac.handler

import com.ac.HttpRequestHandler
import java.io.IOException
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ThreadPoolHttpServerWorker : HttpServerWorker {

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(100)
    override fun stop() {
        threadPool.shutdownNow()
    }

    override fun handleConnection(socket: Socket, httpRequestHandler: HttpRequestHandler) {
        val httpRequestRunner = Runnable {
            try {
                httpRequestHandler.handleConnection(socket)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        threadPool.execute(httpRequestRunner)
    }
}