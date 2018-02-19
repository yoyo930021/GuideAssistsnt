package edu.mcu.team03.guideassistsnt.model

import io.realm.RealmObject

/**
 * Created by yoyo930021 on 2017/12/27.
 */
open class Image : RealmObject() {
    var path: String = ""
    var alt: String? = ""
}