package edu.mcu.team03.guideassistsnt.ui.billing

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Billing
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.billing_item.view.*
import org.jetbrains.anko.textColor


/**
 * Created by yoyo930021 on 2018/1/3.
 */
class BillingAdapter(data: OrderedRealmCollection<Billing>, private val itemClick: (id: String?, long: Boolean) -> Unit) : RealmRecyclerViewAdapter<Billing, BillingAdapter.ViewHolder>(data, true) {



    init {
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        // setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.billing_item, parent, false)
        return ViewHolder(itemView, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = getItem(position)

        holder.bindRecord(obj!!)
    }

    class ViewHolder(view: View, private val itemClick: (id: String?, long: Boolean) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bindRecord(billing: Billing) {
            with(billing) {
                itemView.titleTextView.text = billing.title
                val numUtil = StringBuilder()
                billing.nums.forEachIndexed { index, numUnit ->
                    numUtil.append(numUnit.num)
                    numUtil.append(" ")
                    numUtil.append(numUnit.unit)
                    if ((index+1) != billing.nums.size) numUtil.append(" + ")
                    else numUtil.append(" = ")
                }
                itemView.numUtilTextView.text = numUtil.toString()
                if (billing.type != "請款") {
                    itemView.sumTextView.text = "- ${billing.sum} 元"
                    itemView.sumTextView.textColor = Color.RED
                } else {
                    itemView.sumTextView.text = "+ ${billing.sum} 元"
                    itemView.sumTextView.textColor = Color.BLACK
                }
                itemView.setOnClickListener { itemClick(billing.id, false) }
                itemView.setOnLongClickListener {
                    itemClick(billing.id, true)
                    true
                }
            }
        }
    }
}