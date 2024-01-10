package com.ac.io.handler

import com.ac.io.HttpRequestHandler
import java.net.Socket

interface HttpServerWorker {

    fun stop();
    fun handleConnection(socket: Socket, httpRequestHandler: HttpRequestHandler);
}