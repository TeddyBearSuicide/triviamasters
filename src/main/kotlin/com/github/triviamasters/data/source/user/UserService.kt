package com.github.triviamasters.data.source.user

import com.github.triviamasters.data.models.User
import io.objectbox.Box
import java.util.*

interface UserService {
    fun create(user: User): Boolean
    fun delete(id: UUID): Boolean
    fun delete(user: User): Boolean
    fun get(id: UUID): Boolean
}

class ObjectBoxUserService(
    val users: Box<User>
) : UserService {
    override fun create(user: User): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(id: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(user: User): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(id: UUID): Boolean {
        TODO("Not yet implemented")
    }

}