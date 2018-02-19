package edu.mcu.team03.guideassistsnt.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by yoyo930021 on 2018/1/9.
 */
open class Schedule : RealmObject() {

    @PrimaryKey var id: String = UUID.randomUUID().toString()

    var title: String = ""
    var startTime: Date = Date()
    var finish: Boolean = false

//    var item: RealmList<Item> = RealmList()
    var records: RealmList<Record> = RealmList()
    var billngs: RealmList<Billing> = RealmList()
}