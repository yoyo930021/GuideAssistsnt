package edu.mcu.team03.guideassistsnt.ui.schedule

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Schedule
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.schedule_item.view.*
import java.util.*


/**
 * Created by yoyo930021 on 2018/1/3.
 */
class ScheduleAdapter(data: OrderedRealmCollection<Schedule>, private val itemClick: (id: String?, view: View) -> Unit, private val item: String) : RealmRecyclerViewAdapter<Schedule, ScheduleAdapter.ViewHolder>(data, true) {



    init {
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        // setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.schedule_item, parent, false)
        return ViewHolder(itemView, itemClick, item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = getItem(position)

        holder.bindSchedule(obj!!)
    }

    class ViewHolder(view: View, private val itemClick: (id: String?, view: View) -> Unit, private val item: String) : RecyclerView.ViewHolder(view) {
        fun bindSchedule(schedule: Schedule) {
            with(schedule) {
                itemView.titleTextView.text = schedule.title
                when (item) {
                    "紀錄" -> {
                        itemView.countItemTextView.text = "${schedule.records.size} 個項目"
                    }
                    "記帳" -> {
                        itemView.countItemTextView.text = "${schedule.billngs.size} 項記帳"
                    }
                }

                itemView.finishTextView.text = if(schedule.finish) "已完成" else {
                    if (schedule.startTime.before(Date())) "已開始"
                    else "未開始"
                }
                itemView.scheduleItem.setOnClickListener { itemClick(schedule.id, it) }
            }
        }
    }
}