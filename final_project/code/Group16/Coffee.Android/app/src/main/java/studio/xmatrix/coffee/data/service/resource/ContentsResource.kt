package studio.xmatrix.coffee.data.service.resource

import studio.xmatrix.coffee.data.model.Content

data class ContentsResource(
    val state: String,
    val resource: List<Content>?
)
