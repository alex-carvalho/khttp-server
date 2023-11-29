package com.ac

import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class KHttpServer (private val port: Int) {

    private val routes: MutableMap<String, RequestRunner> = mutableMapOf()
    private val socket = ServerSocket(port)
    private val threadPool: ExecutorService = Executors.newFixedThreadPool(100)
    private val handler: HttpHandler = HttpHandler(routes)

    private lateinit var mainThread: Thread

    fun start () {
        mainThread = Thread {
            println("Server started on port: $port")
            while (true) {
                val clientConnection = socket.accept()
                handleConnection(clientConnection)
            }
        }

        mainThread.start();
    }

    fun stop () {
        mainThread.interrupt()
        threadPool.shutdownNow()
        socket.close()
    }

    private fun handleConnection(clientConnection: Socket) {
        val httpRequestRunner = Runnable {
            try {
                handler.handleConnection(clientConnection.getInputStream(), clientConnection.getOutputStream())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        threadPool.execute(httpRequestRunner)
    }

    fun addRoute(opCode: HttpMethod, route: String, runner: RequestRunner) : KHttpServer {
        routes[opCode.name.plus(route)] = runner
        return this
    }

}


enum class HttpMethod {
    GET,
    PUT,
    POST,
    PATCH
}

enum class HttpStatusCode(val code: Int, val statusMessage: String) {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error")
}
