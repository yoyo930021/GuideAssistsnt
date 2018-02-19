package edu.mcu.team03.guideassistsnt.ui.schedule


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Record
import edu.mcu.team03.guideassistsnt.model.Schedule
import edu.mcu.team03.guideassistsnt.ui.record.RecordAdapter
import edu.mcu.team03.guideassistsnt.ui.record.ViewRecordActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_schedule_item.*


/**
 * A simple [Fragment] subclass.
 */
class ScheduleRecordFragment : Fragment() {


    private var realm: Realm? = null
    private var adapter: RecordAdapter? = null
    private var id: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        realm = Realm.getDefaultInstance()
        id = arguments?.getString("id")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.d("Debug", realm!!.where<Schedule>().equalTo("id", id).findFirst()?.records?.where()!!.findAll().size.toString())

        val openItem = this::openItem
        adapter = RecordAdapter(realm!!.where<Schedule>().equalTo("id", id).findFirst()?.records?.where()!!.findAll(), openItem)
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter
        list.setHasFixedSize(true)
    }

    private fun openItem(id: String?, view: View, long: Boolean){
        if (!long) {
            val intent = Intent(activity, ViewRecordActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("readonly", true)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity as ScheduleActivity, view, "openItem").toBundle())
//            startActivity(intent)
        } else {
            AlertDialog.Builder(activity as ScheduleActivity)
                    .setTitle("你確定")
                    .setIcon(R.drawable.ic_warning)
                    .setMessage("你確定要從這個行程刪除這個紀錄嗎？")
                    .setCancelable(true)
                    .setPositiveButton("確定", { _, _ ->
                        realm?.executeTransaction {
                            val record = it.where<Record>().equalTo("id", id)?.findFirst()
                            val schedule = it.where<Schedule>().equalTo("id", this.id)?.findFirst()
                            schedule?.records?.remove(record)
                        }
                    })
                    .setNegativeButton("取消", {dialog, _ -> dialog.cancel() })
                    .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
        realm = null
    }
}// Required empty public constructor
