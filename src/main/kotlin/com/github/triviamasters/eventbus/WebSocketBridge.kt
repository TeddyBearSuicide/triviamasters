package com.github.triviamasters.eventbus

import com.fasterxml.jackson.databind.ObjectMapper
import io.jooby.Kooby
import io.jooby.WebSocketInitContext
import java.util.concurrent.atomic.AtomicInteger

enum class WebSocketEventType(val type: String) {
    Publish("publish"),
    Register("register"),
    Request("request"),
    Send("send"),
    Unregister("unregister")
}

data class WebSocketEventMessage(
    val type: WebSocketEventType,
    val payload: Message
)

data class WebSocketListener(
    val counter: AtomicInteger,
    val listener: DisposableEventListener
)

class WebSocketBridge(
    private val eventBus: EventBus
) {

    private val mapper = ObjectMapper()

    val websocketInit: WebSocketInitContext.() -> Any = {

        val listeners: HashMap<String, WebSocketListener> = hashMapOf()

        configurer.onConnect {
            println("Client as connected")
        }

        configurer.onMessage { ws, message ->
            val node = mapper.readTree(message.value())

            println("Received message: $node")

            if (!node.isObject) {
                ws.send("Invalid message: is not an object")
                return@onMessage
            }

            if (!node.has("type")) {
                ws.send("Invalid message: missing 'type' field")
                return@onMessage
            }

            if (!node.has("payload")) {
                ws.send("Invalid message: missing 'payload' field")
                return@onMessage
            }

            val messageType = node.get("type").asText().let { type -> WebSocketEventType.values().firstOrNull { it.type == type } }
            val payload = node.get("payload")

            if (messageType == null) {
                ws.send("Invalid message type: '${node.get("type").asText()}' is not an acceptable type")
                return@onMessage
            }

            val address = payload.get("address")?.asText()

            if (address == null) {
                ws.send("Invalid message address: it is null")
                return@onMessage
            }

            when (messageType) {

                WebSocketEventType.Publish -> {
                    eventBus.publish(address, payload)
                }

                WebSocketEventType.Register -> {
                    val webSocketListener = listeners[address]

                    if (webSocketListener == null) {
                        listeners[address] = WebSocketListener(
                            counter = AtomicInteger(1),
                            listener = eventBus.register(address) {
                                ws.send(mapper.writeValueAsString(it))
                            }
                        )
                    }
                }

                WebSocketEventType.Request -> {
                    eventBus.request(address, payload) {
                        ws.send(mapper.writeValueAsString(it))
                    }
                }

                WebSocketEventType.Send -> {
                    eventBus.send(address, payload)
                }

                WebSocketEventType.Unregister -> {
                    val webSocketListener = listeners[address]

                    if (webSocketListener != null && webSocketListener.counter.decrementAndGet() == 0) {
                        webSocketListener.listener.dispose()
                        listeners.remove(address)
                    }
                }
            }
        }

        configurer.onClose { _, _ ->
            listeners.values.forEach {
                it.listener.dispose()
            }
            listeners.clear()
        }
    }
}

fun Kooby.addWebSocketBridge(path: String, eventBus: EventBus) = apply {
    val bridge = WebSocketBridge(eventBus)
    ws(path, bridge.websocketInit)
}