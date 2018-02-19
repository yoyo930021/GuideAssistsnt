package edu.mcu.team03.guideassistsnt.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


/**
 * Created by yoyo930021 on 2017/12/27.
 */
open class Record : RealmObject() {

    @PrimaryKey var id: String = UUID.randomUUID().toString()

    var title: String = ""
    var type: String = ""
    var telephone: String? = null
//    var money: Int? = null
//    var openTime: String? = null
    var rating: Double? = null
    var website: String? = null
    var content: String? = null
    var county: String? = null
    var address: String? = null

    var images: RealmList<Image> = RealmList()
}