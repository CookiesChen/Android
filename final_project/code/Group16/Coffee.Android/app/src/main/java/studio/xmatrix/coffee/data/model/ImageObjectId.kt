package studio.xmatrix.coffee.data.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
class ImageObjectId constructor() {

    @Id
    var _id: Long = 0

    @Unique
    lateinit var id: String

    constructor(id: String) : this() {
        this.id = id
    }
}
