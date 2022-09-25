package com.github.triviamasters.di

import com.github.triviamasters.App
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::App)
}