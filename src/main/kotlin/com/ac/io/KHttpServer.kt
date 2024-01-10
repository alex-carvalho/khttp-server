package com.ac.io

import com.ac.RequestRunner
import com.ac.io.handler.HttpServerWorker
import com.ac.io.handler.ThreadPoolHttpServerWorker
import java.net.ServerSocket
import java.net.Socket


class KHttpServer (private val port: Int, private val worker: HttpServerWorker = ThreadPoolHttpServerWorker()) {

    private val routes: MutableMap<String, RequestRunner> = mutableMapOf()
    private val socket = ServerSocket(port)
    private val handler: HttpRequestHandler = HttpRequestHandler(routes)

    private lateinit var mainThread: Thread

    fun start () {
        mainThread = Thread {
            println("Server started on port: $port")
            while (true) {
                val socket = socket.accept()
                handleConnection(socket)
            }
        }

        mainThread.start();
    }

    fun stop () {
        mainThread.interrupt()
        worker.stop()
        socket.close()
    }

    private fun handleConnection(clientConnection: Socket) {
        worker.handleConnection(clientConnection, handler)
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
