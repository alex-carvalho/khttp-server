package com.ac

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*


fun  main() {
    val server = KHttpServer(8080, CachedThreadPoolHttpServerWorker())
        .addRoute(HttpMethod.GET, "/" ){
            HttpResponse.ok(UUID.randomUUID().toString())
        }

    server.start()


    val rt = Runtime.getRuntime()
    val commands = arrayOf("k6", "run", "/Users/alex/workspace/khttp-server/src/test/resources/load-test.js")
    val proc = rt.exec(commands)

    val stdInput = BufferedReader(InputStreamReader(proc.inputStream))

    val stdError = BufferedReader(InputStreamReader(proc.errorStream))

// Read the output from the command

// Read the output from the command
    println("Here is the standard output of the command:\n")
    var s: String? = null
    while (stdInput.readLine().also { s = it } != null) {
        println(s)
    }

// Read any errors from the attempted command

// Read any errors from the attempted command
    println("Here is the standard error of the command (if any):\n")
    while (stdError.readLine().also { s = it } != null) {
        println(s)
    }

}