package edu.mcu.team03.guideassistsnt.ui.billing


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Schedule
import edu.mcu.team03.guideassistsnt.ui.MainActivity
import edu.mcu.team03.guideassistsnt.ui.schedule.ScheduleAdapter
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_schedule.*


/**
 * A simple [Fragment] subclass.
 */
class BillingFragment : Fragment() {

    private var realm: Realm? = null
    private var adapter: ScheduleAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        realm = Realm.getDefaultInstance()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_billing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ScheduleAdapter(realm!!.where<Schedule>().sort(arrayOf("finish","startTime"), arrayOf(Sort.ASCENDING, Sort.ASCENDING)).findAll(), this::openItem, "記帳")
        schedule_list.layoutManager = LinearLayoutManager(activity?.applicationContext)
        schedule_list.adapter = adapter
        schedule_list.setHasFixedSize(true)
    }

    private fun openItem(id: String?, view: View){
        val intent = Intent(activity as MainActivity, BillingActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity as MainActivity, view, "openItem").toBundle())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
        realm = null
    }

}// Required empty public constructor
