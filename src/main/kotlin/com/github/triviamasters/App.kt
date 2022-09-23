package com.github.triviamasters

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.triviamasters.eventbus.*
import io.jooby.Kooby
import io.jooby.json.JacksonModule
import io.jooby.runApp

class App: Kooby({

    install(JacksonModule())

    val mapper = ObjectMapper()

    val eventBus: EventBus = LocalEventBus(mapper)
    eventBus.register("test") {
        println("Got a test message: ${it.body}")
    }

    addWebSocketBridge("/eventbus", eventBus)

    get("/") {
        val body = ObjectMapper()
            .createObjectNode()
            .put("firstName", "Jacob")
            .put("lastName", "Boss")

        eventBus.publish("test", body)
    }
})

fun main(args: Array<String>) {
  runApp(args, App::class)
}
