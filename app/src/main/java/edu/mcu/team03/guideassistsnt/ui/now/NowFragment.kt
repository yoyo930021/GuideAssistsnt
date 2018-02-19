package edu.mcu.team03.guideassistsnt.ui.now


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Record
import edu.mcu.team03.guideassistsnt.model.Schedule
import edu.mcu.team03.guideassistsnt.ui.MainActivity
import edu.mcu.team03.guideassistsnt.ui.billing.EditBillingActivity
import edu.mcu.team03.guideassistsnt.ui.record.ChooseRecordActivity
import edu.mcu.team03.guideassistsnt.ui.schedule.ScheduleActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_now.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class NowFragment : Fragment() {

    private var realm: Realm? = null
    private var adapter: NowAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        realm = Realm.getDefaultInstance()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_now, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NowAdapter(realm!!.where<Schedule>().equalTo("finish", false).lessThan("startTime", Date()).findAll(), this::openItem, this::actionRecord, this::actionBilling)
        schedule_list.layoutManager = LinearLayoutManager(activity?.applicationContext)
        schedule_list.adapter = adapter
        schedule_list.setHasFixedSize(true)
    }

    private fun openItem(id: String?, view: View){
        val intent = Intent(activity as MainActivity, ScheduleActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity as MainActivity, view, "openItem").toBundle())
    }

    private fun actionRecord(id: String?){
        val intent = Intent(activity as MainActivity, ChooseRecordActivity::class.java)
        intent.putExtra("id", id)
        startActivityForResult(intent, 1)
    }

    private fun actionBilling(id: String?){
        val intent = Intent(activity as MainActivity, EditBillingActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("action", "add")
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                data?.let {
                    realm?.executeTransaction { realm ->
                        val item = realm.where<Record>().equalTo("id", data.extras.getString("id")).findFirst()
                        val schedule = realm.where<Schedule>().equalTo("id", data.extras.getString("scheduleID"))?.findFirst()
                        schedule?.records!!.add(item)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
        realm = null
    }

}// Required empty public constructor
