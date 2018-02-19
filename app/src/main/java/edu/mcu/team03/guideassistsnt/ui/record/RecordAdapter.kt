package edu.mcu.team03.guideassistsnt.ui.record

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.mcu.team03.guideassistsnt.GlideApp
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Record
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.record_item.view.*


/**
 * Created by yoyo930021 on 2018/1/3.
 */
class RecordAdapter(data: OrderedRealmCollection<Record>, private val itemClick: (id: String?, view: View, long: Boolean) -> Unit) : RealmRecyclerViewAdapter<Record, RecordAdapter.ViewHolder>(data, true) {



    init {
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        // setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.record_item, parent, false)
        return ViewHolder(itemView, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = getItem(position)

        holder.bindRecord(obj!!)
    }

    class ViewHolder(view: View, private val itemClick: (id: String?, view: View, long: Boolean) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bindRecord(record: Record) {
            with(record) {
                itemView.titleTextView.text = record.title
                itemView.telTextView.text = record.telephone
                itemView.AdrTextView.text = "${record.county}${record.address}"
                if (!record.images.isEmpty()) {
                    GlideApp.with(itemView.context).load(record.images[0]?.path).centerCrop().into(itemView.HeaderImageView)
                    itemView.recordItem.setOnClickListener { itemClick(record.id, itemView.HeaderImageView, false) }
                    itemView.recordItem.setOnLongClickListener {
                        itemClick(record.id, itemView.HeaderImageView, true)
                        true
                    }
                } else {
                    itemView.recordItem.setOnClickListener { itemClick(record.id, it, false) }
                    itemView.recordItem.setOnLongClickListener {
                        itemClick(record.id, it, true)
                        true
                    }
                }
            }
        }
    }
}