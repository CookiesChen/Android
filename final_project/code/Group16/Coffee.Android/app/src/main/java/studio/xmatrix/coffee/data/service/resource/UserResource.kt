package studio.xmatrix.coffee.data.service.resource

import studio.xmatrix.coffee.data.model.User

data class UserResource(
    val state: String,
    val resource: User?
)
