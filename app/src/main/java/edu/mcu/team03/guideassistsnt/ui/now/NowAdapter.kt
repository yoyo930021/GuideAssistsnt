package edu.mcu.team03.guideassistsnt.ui.now

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Schedule
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.now_item.view.*


/**
 * Created by yoyo930021 on 2018/1/3.
 */
class NowAdapter(data: OrderedRealmCollection<Schedule>, private val itemClick: (id: String?, view: View) -> Unit, private val actionRecord: (id: String?) -> Unit, private val actionBilling: (id: String?) -> Unit) : RealmRecyclerViewAdapter<Schedule, NowAdapter.ViewHolder>(data, true) {



    init {
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        // setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.now_item, parent, false)
        return ViewHolder(itemView, itemClick, actionRecord, actionBilling)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = getItem(position)

        holder.bindSchedule(obj!!)
    }

    class ViewHolder(view: View, private val itemClick: (id: String?, view: View) -> Unit, private val actionRecord: (id: String?) -> Unit, private val actionBilling: (id: String?) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bindSchedule(schedule: Schedule) {
            with(schedule) {
                itemView.titleTextView.text = schedule.title
                itemView.countItemTextView.text = "${schedule.records.size}個紀錄  ${schedule.billngs.size}項記帳"
                itemView.scheduleItem.setOnClickListener { itemClick(schedule.id, it) }
                itemView.record_action.setOnClickListener { actionRecord(schedule.id) }
                itemView.billing_action.setOnClickListener { actionBilling(schedule.id) }
            }
        }
    }
}