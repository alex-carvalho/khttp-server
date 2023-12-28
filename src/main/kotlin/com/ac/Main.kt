package com.ac

import com.ac.nio.KHttpServerNIO

import java.util.*


fun  main() {
//    val server = KHttpServer(8080, ThreadPoolHttpServerWorker())
//        .addRoute(HttpMethod.GET, "/" ){
//            HttpResponse.ok(UUID.randomUUID().toString())
//        }
//
//    server.start()

//    val server = KHttpServer(8080, ThreadPoolHttpServerWorker(Executors.newVirtualThreadPerTaskExecutor()))
//        .addRoute(HttpMethod.GET, "/" ){
//            HttpResponse.ok(UUID.randomUUID().toString())
//        }
//
//    server.start()

    val server = KHttpServerNIO(8080)
        .addRoute(HttpMethod.GET, "/" ){
            HttpResponse.ok(UUID.randomUUID().toString())
        }
        .addRoute(HttpMethod.GET, "/hello" ){
            HttpResponse.ok("hello")
        }

    server.start()
}