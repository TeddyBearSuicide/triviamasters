package com.github.triviamasters.di

import com.github.triviamasters.eventbus.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val eventBusModule = module {
    singleOf(::LocalEventBus) bind EventBus::class
}