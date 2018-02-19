package edu.mcu.team03.guideassistsnt.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by yoyo930021 on 2018/1/11.
 */
open class Billing : RealmObject() {
    @PrimaryKey var id: String = UUID.randomUUID().toString()

    var title: String = ""
    var type: String = ""
    var nums: RealmList<NumUnit> = RealmList()
    var sum: Int = 0
    var content: String? = null
}