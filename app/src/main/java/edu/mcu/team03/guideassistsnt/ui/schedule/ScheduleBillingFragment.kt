package edu.mcu.team03.guideassistsnt.ui.schedule


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Schedule
import edu.mcu.team03.guideassistsnt.ui.billing.BillingAdapter
import edu.mcu.team03.guideassistsnt.ui.record.RecordAdapter
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_billing_item.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent


/**
 * A simple [Fragment] subclass.
 */
class ScheduleBillingFragment : Fragment() {


    private var realm: Realm? = null
    private var adapter: RecordAdapter? = null
    private var id: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        realm = Realm.getDefaultInstance()
        id = arguments?.getString("id")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_billing_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }
    public fun initView() {
        realm?.let {
            val schedule = realm!!.where<Schedule>().equalTo("id", id).findFirst()
            val billings = schedule?.billngs!!

            billing_list.removeAllViews()

            billing_list.addView(makeList("請款", BillingAdapter(billings.where().equalTo("type","請款").findAll(), this::openItem), billings.where().equalTo("type","請款").sum("sum"), false))
            billing_list.addView(makeList("交通", BillingAdapter(billings.where().equalTo("type","交通").findAll(), this::openItem), billings.where().equalTo("type","交通").sum("sum"), true))
            billing_list.addView(makeList("景點", BillingAdapter(billings.where().equalTo("type","景點").findAll(), this::openItem), billings.where().equalTo("type","景點").sum("sum"), true))
            billing_list.addView(makeList("住宿", BillingAdapter(billings.where().equalTo("type","住宿").findAll(), this::openItem), billings.where().equalTo("type","住宿").sum("sum"), true))
            billing_list.addView(makeList("餐飲", BillingAdapter(billings.where().equalTo("type","餐飲").findAll(), this::openItem), billings.where().equalTo("type","餐飲").sum("sum"), true))
            billing_list.addView(makeList("預備金", BillingAdapter(billings.where().equalTo("type","預備金").findAll(), this::openItem), billings.where().equalTo("type","預備金").sum("sum"), true))
            var sum: Int = billings.where().equalTo("type","請款").sum("sum").toInt()
            sum -= billings.where().equalTo("type","交通").sum("sum").toInt()
            sum -= billings.where().equalTo("type","景點").sum("sum").toInt()
            sum -= billings.where().equalTo("type","住宿").sum("sum").toInt()
            sum -= billings.where().equalTo("type","餐飲").sum("sum").toInt()
            sum -= billings.where().equalTo("type","預備金").sum("sum").toInt()
            billing_list.addView(makeSum(sum))
        }

    }

    private fun makeList(title: String, inAdapter: BillingAdapter, sum: Number, cost: Boolean): View {
        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(matchParent, wrapContent)
        linearLayout.layoutParams = params
        val titleTextView = AppCompatTextView(activity)
        titleTextView.text = title
        titleTextView.textColor = Color.WHITE
        titleTextView.backgroundColor = Color.parseColor("#22b294")
        titleTextView.textSize = 24f
        titleTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        titleTextView.setPadding(dip(32), 0, 0, 0)
        titleTextView.layoutParams = ViewGroup.LayoutParams(matchParent, dip(50))
        linearLayout.addView(titleTextView)
        val list = RecyclerView(activity)
        list.setPadding(dip(32), 0, dip(32), 0)
        list.layoutManager = LinearLayoutManager(activity)
        list.setHasFixedSize(true)
        list.adapter = inAdapter
        list.layoutParams = params
        linearLayout.addView(list)
        val driver2 = LinearLayout(activity)
        driver2.backgroundColor = Color.DKGRAY
        driver2.layoutParams = ViewGroup.LayoutParams(matchParent, dip(1))
        linearLayout.addView(driver2)
        val sumTextView = AppCompatTextView(activity)
        sumTextView.setPadding(0, 0, dip(32), 0)
        if (cost) {
            sumTextView.text = "- ${sum.toString()} 元"
            sumTextView.textColor = Color.RED
        } else {
            sumTextView.text = "+ ${sum.toString()} 元"
            sumTextView.textColor = Color.BLACK
        }
        sumTextView.textSize = 18f
        sumTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        sumTextView.layoutParams = ViewGroup.LayoutParams(matchParent, dip(30))
        linearLayout.addView(sumTextView)
        return linearLayout
    }

    private fun makeSum(sum: Int): View {
        val cost = (sum < 0)
        val sumTextView = AppCompatTextView(activity)
        sumTextView.setPadding(0, 0, dip(32), 0)
        sumTextView.text = "總共 ${sum} 元"
        if (cost) {
            sumTextView.textColor = Color.RED
        } else {
            sumTextView.textColor = Color.BLACK
        }
        sumTextView.textSize = 18f
        sumTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        return  sumTextView
    }

    private fun openItem(id: String?, long: Boolean){
//            val intent = Intent(activity as MainActivity, BillingActivity::class.java)
//            intent.putExtra("id", id)
//        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity as MainActivity, view, "openItem").toBundle())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
        realm = null
    }
}// Required empty public constructor
