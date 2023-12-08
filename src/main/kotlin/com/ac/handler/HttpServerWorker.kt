package com.ac.handler

import com.ac.HttpRequestHandler
import java.net.Socket

interface HttpServerWorker {

    fun stop();
    fun handleConnection(socket: Socket, httpRequestHandler: HttpRequestHandler);
}