package com.ac.nio

import com.ac.*
import com.ac.io.HttpMethod
import com.ac.io.HttpStatusCode
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.function.Function

typealias RequestRunner = Function<HttpRequest, HttpResponse>

class KHttpServerNIO(private val port: Int) {
    private lateinit var serverChannel: ServerSocketChannel
    private lateinit  var selector: Selector
    private val routes: MutableMap<String, RequestRunner> = mutableMapOf()

    fun start() {
        init()
        startLoop()

    }


    private fun init() {
        selector = Selector.open()
        serverChannel = ServerSocketChannel.open()
        serverChannel.configureBlocking(false)
        serverChannel.socket().bind(InetSocketAddress(port))
        serverChannel.register(selector, SelectionKey.OP_ACCEPT)
        println("Server started on port: $port")
    }

    private fun stop() {
        selector.close()
        serverChannel.close()
    }


    fun addRoute(opCode: HttpMethod, route: String, runner: RequestRunner) : KHttpServerNIO {
        routes[opCode.name.plus(route)] = runner
        return this
    }

    private fun startLoop() {
        while (true) {
            selector.select()
            val keys = selector.selectedKeys()
            val keyIterator = keys.iterator()
            while (keyIterator.hasNext()) {
                val key = keyIterator.next()
                keyIterator.remove()
                try {
                    if (!key.isValid) {
                        continue
                    }
                    if (key.isAcceptable) {
                        accept()
                    } else if (key.isReadable) {
                        read(key)
                    } else if (key.isWritable) {
                        write(key)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    closeChannelSilently(key)
                }
            }
        }
    }

    private fun accept() {
        val clientChannel = serverChannel.accept()
        if (clientChannel == null) {
           println("No connection is available. Skipping selection key")
            return
        }
        clientChannel.configureBlocking(false)
        clientChannel.register(selector, SelectionKey.OP_READ)
    }

    private fun read(key: SelectionKey) {
        val clientChannel = key.channel() as SocketChannel
        val request = HttpRequest.decode(clientChannel)

        key.attach(request)
        println("Parsed incoming HTTP request: $request")
        key.interestOps(SelectionKey.OP_WRITE)
    }


    private fun write(key: SelectionKey) {
        val clientChannel = key.channel() as SocketChannel
        val request = key.attachment() as HttpRequest

        val routeKey: String = request.httpMethod.name.plus(request.uri.rawPath)
        if (routes.containsKey(routeKey)) {
            val httpResponse = routes[routeKey]!!.apply(request)
            httpResponse.writeTo(clientChannel)
        } else {
            HttpResponse(HttpStatusCode.NOT_FOUND, "Route Not Found...", mutableMapOf())
                .writeTo(clientChannel)
        }
        closeChannelSilently(key)
    }

    private fun closeChannelSilently(key: SelectionKey) {
        val channel = key.channel()
        key.cancel()
        println("Closing connection for channel: $channel")
        channel.close()
    }
}