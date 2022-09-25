package com.github.triviamasters.di

import com.github.triviamasters.data.models.MyObjectBox
import com.github.triviamasters.data.models.User
import com.github.triviamasters.data.source.user.ObjectBoxUserService
import com.github.triviamasters.data.source.user.UserService
import io.objectbox.BoxStore
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        return@single MyObjectBox.builder().name("objectbox-db").build()
    }

    factory {
        named("users")
        return@factory get<BoxStore>().boxFor(User::class.java)
    }

    factory {
        return@factory ObjectBoxUserService(get(named("users")))
    } bind UserService::class
}