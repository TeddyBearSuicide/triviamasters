package com.github.triviamasters.eventbus

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.UUID

data class Message(
    val address: String,
    val body: JsonNode,
    val headers: HashMap<String, String>,
    val replyAddress: String?
)

fun interface EventConsumer {
    operator fun invoke(message: Message)
}

data class DisposableEventListener(
    val address: String,
    val consumer: EventConsumer,
    private val disposer: DisposableEventListener.() -> Unit
) {
    fun dispose() {
        disposer()
    }
}

interface EventBus {
    fun publish(address: String, body: JsonNode)
    fun send(address: String, body: JsonNode)
    fun register(address: String, consumer: EventConsumer): DisposableEventListener
    fun request(address: String, body: JsonNode, responseHandler: (Message) -> Unit)
    fun unregister(listener: DisposableEventListener)
}

class LocalEventBus(
    private val mapper: ObjectMapper
) : EventBus {

    private val listeners: HashMap<String, HashSet<DisposableEventListener>> = HashMap()

    override fun publish(address: String, body: JsonNode) {
        val message = Message(address, body, hashMapOf(), null)

        listeners[address]
            ?.forEach { it.consumer(message) }
    }

    override fun send(address: String, body: JsonNode) {
        val listener = listeners[address]?.random()

        if (listener != null) {
            val message = Message(address, body, hashMapOf(), null)
            listener.consumer(message)
        }
    }

    override fun register(address: String, consumer: EventConsumer): DisposableEventListener {

        val listener = DisposableEventListener(
            address = address,
            consumer = consumer,
            disposer = {
                listeners[address]?.remove(this)
        })

        listeners.putIfAbsent(address, hashSetOf(listener))?.add(listener)

        return listener
    }

    override fun request(address: String, body: JsonNode, responseHandler: (Message) -> Unit) =

        when (val listener = listeners[address]?.random()) {

            is DisposableEventListener -> {
                val replyAddress = UUID.randomUUID().toString()
                val message = Message(address, body, hashMapOf(), replyAddress)

                lateinit var disposableEventListener: DisposableEventListener
                disposableEventListener = register(replyAddress) {
                    responseHandler(it)
                    disposableEventListener.dispose()
                }

                listener.consumer(message)
            }

            else -> {}
        }

    override fun unregister(listener: DisposableEventListener) {
        listener.dispose()
    }
}