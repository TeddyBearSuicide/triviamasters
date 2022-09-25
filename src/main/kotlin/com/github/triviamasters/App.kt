package com.github.triviamasters

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.triviamasters.di.appModule
import com.github.triviamasters.di.databaseModule
import com.github.triviamasters.di.eventBusModule
import com.github.triviamasters.di.objectMapperModule
import com.github.triviamasters.eventbus.*
import io.jooby.Kooby
import io.jooby.runApp
import org.koin.core.context.startKoin

class App: Kooby({

    val services = startKoin {
        modules(
            appModule,
            objectMapperModule,
            eventBusModule,
            databaseModule
        )
    }

    val eventBus = services.koin.get<EventBus>()
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
