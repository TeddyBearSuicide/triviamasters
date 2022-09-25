package com.github.triviamasters.di

import com.fasterxml.jackson.databind.ObjectMapper
import org.koin.dsl.module

val objectMapperModule = module {
    single {
        ObjectMapper()
    }
}