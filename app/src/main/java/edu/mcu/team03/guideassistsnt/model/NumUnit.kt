package edu.mcu.team03.guideassistsnt.model

import io.realm.RealmObject

/**
 * Created by yoyo930021 on 2018/1/11.
 */
open class NumUnit : RealmObject() {
    var num: Int = 0
    var unit: String = "元"
}