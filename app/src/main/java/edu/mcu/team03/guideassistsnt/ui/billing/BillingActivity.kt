package edu.mcu.team03.guideassistsnt.ui.billing

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Schedule
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_billing.*
import org.jetbrains.anko.*



class BillingActivity : AppCompatActivity() {

    private var realm: Realm? = null
    private var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realm = Realm.getDefaultInstance()

        val bundle = this.intent.extras
        id = bundle.getString("id")

        val schedule = realm!!.where<Schedule>().equalTo("id", id).findFirst()
        title = schedule?.title

        initView()

        srcollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if( scrollY != 0 ) {
                fab.hide()
            } else {
                fab.show()
            }
        }

        fab.setOnClickListener {
            startActivity<EditBillingActivity>("action" to "add", "id" to id)
        }
    }

    private fun initView() {
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

    private fun makeList(title: String, inAdapter: BillingAdapter, sum: Number, cost: Boolean): View {
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(matchParent, wrapContent)
        linearLayout.layoutParams = params
        val titleTextView = AppCompatTextView(this)
        titleTextView.text = title
        titleTextView.textColor = Color.WHITE
        titleTextView.backgroundColor = Color.parseColor("#22b294")
        titleTextView.textSize = 24f
        titleTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        titleTextView.setPadding(dip(32), 0, 0, 0)
        titleTextView.layoutParams = ViewGroup.LayoutParams(matchParent, dip(50))
        linearLayout.addView(titleTextView)
        val list = RecyclerView(this)
        list.setPadding(dip(32), 0, dip(32), 0)
        list.layoutManager = LinearLayoutManager(this@BillingActivity)
        list.setHasFixedSize(true)
        list.adapter = inAdapter
        list.layoutParams = params
        linearLayout.addView(list)
        val driver2 = LinearLayout(this)
        driver2.backgroundColor = Color.DKGRAY
        driver2.layoutParams = ViewGroup.LayoutParams(matchParent, dip(1))
        linearLayout.addView(driver2)
        val sumTextView = AppCompatTextView(this)
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
        val sumTextView = AppCompatTextView(this)
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

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun openItem(id: String?, long: Boolean){
//            val intent = Intent(activity as MainActivity, BillingActivity::class.java)
//            intent.putExtra("id", id)
//        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity as MainActivity, view, "openItem").toBundle())
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }
}
