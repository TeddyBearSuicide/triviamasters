package com.github.triviamasters.data.models

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class User(
    var bgImageSrc: String,
    @Id
    var id: Long,
    var lifetimePoints: Long,
    var name: String,
    var profileImageSrc: String,
    var rank: String,
    var status: String,
    var username: String
)