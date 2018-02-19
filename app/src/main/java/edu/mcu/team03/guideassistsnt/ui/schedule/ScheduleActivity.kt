package edu.mcu.team03.guideassistsnt.ui.schedule

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.view.*
import android.widget.LinearLayout
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Record
import edu.mcu.team03.guideassistsnt.model.Schedule
import edu.mcu.team03.guideassistsnt.ui.billing.EditBillingActivity
import edu.mcu.team03.guideassistsnt.ui.record.ChooseRecordActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_schedule.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import java.text.SimpleDateFormat
import java.util.*



class ScheduleActivity : AppCompatActivity() {

    private var realm: Realm? = null
    private var id: String? = null
    private var adapter: ScheduleFragmentAdapter? = null
    private var lastPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realm = Realm.getDefaultInstance()

        val bundle = this.intent.extras
        id = bundle.getString("id")

        val schedule = realm?.where<Schedule>()?.equalTo("id", id)?.findFirst()
        collapsing_toolbar.title = schedule?.title

        scheduleViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                lastPosition = position
                if (lastPosition == 0) {
                    fab.imageResource = R.drawable.ic_add
                } else {
                    fab.imageResource = R.drawable.ic_bill_white
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        adapter = ScheduleFragmentAdapter(supportFragmentManager)
        val recordList = ScheduleRecordFragment()
        val billingList = ScheduleBillingFragment()
        val sendBundle = Bundle()
        sendBundle.putString("id", id)
        recordList.arguments = sendBundle
        billingList.arguments = sendBundle
        adapter?.addFragment(recordList, "紀錄")
        adapter?.addFragment(billingList, "記帳")
        scheduleViewPager.adapter = adapter
        tabs.setupWithViewPager(scheduleViewPager)

//        adapter = ScheduleItemAdapter(schedule?.item!!, this::openItem)
//        schedule_item_list.layoutManager = LinearLayoutManager(this)
//        schedule_item_list.adapter = adapter
//        schedule_item_list.setHasFixedSize(true)

        fab.setOnClickListener {
            if (lastPosition == 0) {
                val intent = Intent(this, ChooseRecordActivity::class.java)
                intent.putExtra("id", id)
                startActivityForResult(intent, 1)
            } else {
                val intent = Intent(this, EditBillingActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("action", "add")
                startActivity(intent)
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_schedule, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        val schedule = realm?.where<Schedule>()?.equalTo("id", id)?.findFirst()
        return when (item.itemId) {
            R.id.action_check -> {
                AlertDialog.Builder(this)
                        .setMessage("你確定要標記已完成嗎？")
                        .setCancelable(true)
                        .setPositiveButton("確定", { _, _ ->
                            realm?.executeTransaction {
                                schedule?.finish = true
                                finish()
                            }
                        })
                        .setNegativeButton("取消", {dialog, _ -> dialog.cancel() })
                        .show()
                true
            }
            R.id.action_edit -> {
                val df = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val customView = createDialog(schedule!!.title, df.format(schedule.startTime))
                android.app.AlertDialog.Builder(this)
                        .setTitle("修改行程")
                        .setView(customView)
                        .setCancelable(true)
                        .setPositiveButton("確定",  { dialog, _ ->
                            val name = ((((customView as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0) as TextInputEditText).text.toString()
                            val date = (customView.getChildAt(1) as AppCompatEditText).text.toString()
                            if (name != ""){
                                realm?.executeTransaction {
                                    schedule.title = name
                                    schedule.finish = false
                                    if (date != "") schedule.startTime = df.parse(date)
                                }
                            }
                        })
                        .setNegativeButton("取消", { dialog, _ -> dialog.cancel() })
                        .show()
                true
            }
            R.id.action_del -> {
                AlertDialog.Builder(this)
                        .setTitle("你確定")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("你確定要刪除這個行程嗎？")
                        .setCancelable(true)
                        .setPositiveButton("確定", { _, _ ->
                            realm?.executeTransaction {
                                schedule?.deleteFromRealm()
                                finish()
                            }
                        })
                        .setNegativeButton("取消", {dialog, _ -> dialog.cancel() })
                        .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createDialog(name: String, startTime: String): View {
        val layout = LinearLayout(ContextThemeWrapper(applicationContext, R.style.AppTheme))
        layout.layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        layout.setPadding(dip(16), dip(16), dip(16), dip(16))
        layout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(matchParent, matchParent)
        params.topMargin = 16

        val nameTil = TextInputLayout(ContextThemeWrapper(applicationContext, R.style.AppTheme))
        nameTil.layoutParams = params
        val nameEditText = TextInputEditText(ContextThemeWrapper(applicationContext, R.style.AppTheme))
        nameEditText.layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
        nameEditText.hint = "行程名稱"
        nameEditText.setSingleLine(true)
        nameEditText.setText(name)
        nameTil.addView(nameEditText)
        layout.addView(nameTil)

        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        sdf.parse(startTime)
        val myCalendar = sdf.calendar


        val dateEditText = AppCompatEditText(ContextThemeWrapper(applicationContext, R.style.AppTheme))
        dateEditText.layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
        dateEditText.hint = "行程開始日期"
        dateEditText.isClickable = true
        dateEditText.isFocusable = false
        dateEditText.setSingleLine(true)
        dateEditText.setText(startTime)
        dateEditText.setOnClickListener {
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                dateEditText.setText(sdf.format(myCalendar.time))
            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        layout.addView(dateEditText)

        return layout
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                data?.let {
                    realm?.executeTransaction { realm ->
                        val item = realm.where<Record>().equalTo("id", data.extras.getString("id")).findFirst()
                        val schedule = realm.where<Schedule>().equalTo("id", id)?.findFirst()
                        schedule?.records!!.add(item)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (adapter!!.getItem(1) as ScheduleBillingFragment).initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }
}
