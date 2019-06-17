package studio.xmatrix.coffee.data.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
class User constructor() {

    @Id
    var _id: Long = 0

    @Unique
    lateinit var id: String
    lateinit var email: String
    var userClass: Int = ClassBlackUser

    // UserInfo
    lateinit var name: String
    lateinit var avatar: String
    lateinit var bio: String
    var gender: Int = GenderUnknown

    var maxSize: Long = 0
    var usedSize: Long = 0
    var singleSize: Long = 0

    companion object {
        // User Class
        const val ClassBlackUser = 0
        const val ClassLimitUser = 1
        const val ClassNormalUser = 2
        const val ClassVerifyUser = 3
        const val ClassVIPUser = 4
        const val ClassSVIPUser = 5
        const val ClassAdmin = 6
        const val ClassSAdmin = 7

        // Gender
        const val GenderMan = 0
        const val GenderWoman = 1
        const val GenderUnknown = 2
    }

    constructor(
        id: String,
        email: String,
        userClass: Int,
        name: String,
        avatar: String,
        bio: String,
        gender: Int,
        maxSize: Long,
        usedSize: Long,
        singleSize: Long
    ) : this() {
        this.id = id
        this.email = email
        this.userClass = userClass
        this.name = name
        this.avatar = avatar
        this.bio = bio
        this.gender = gender
        this.maxSize = maxSize
        this.usedSize = usedSize
        this.singleSize = singleSize
    }
}
