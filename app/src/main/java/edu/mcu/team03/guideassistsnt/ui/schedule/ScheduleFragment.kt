package edu.mcu.team03.guideassistsnt.ui.schedule


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.LinearLayoutManager
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Schedule
import edu.mcu.team03.guideassistsnt.ui.MainActivity
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.dip
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ScheduleFragment : Fragment() {

    private var realm: Realm? = null
    private var adapter: ScheduleAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        realm = Realm.getDefaultInstance()
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ScheduleAdapter(realm!!.where<Schedule>().sort(arrayOf("finish","startTime"), arrayOf(Sort.ASCENDING, Sort.ASCENDING)).findAll(), this::openItem, "紀錄")
        schedule_list.layoutManager = LinearLayoutManager(activity?.applicationContext)
        schedule_list.adapter = adapter
        schedule_list.setHasFixedSize(true)


        fab.setOnClickListener {
            val customView = createDialog()
            AlertDialog.Builder(activity)
                    .setTitle("新增行程")
                    .setView(customView)
                    .setCancelable(true)
                    .setPositiveButton("確定",  { _, _ ->
                        val name = ((((customView as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0) as TextInputEditText).text.toString()
                        val date = (customView.getChildAt(1) as AppCompatEditText).text.toString()
                        val df = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                        if (name != ""){
                            realm?.executeTransaction {
                                val schedule = it.createObject(Schedule::class.java, UUID.randomUUID().toString())
                                schedule.title = name
                                schedule.finish = false
                                if (date != "") schedule.startTime = df.parse(date)
                            }
                        }
                    })
                    .setNegativeButton("取消", { dialog, _ -> dialog.cancel() })
                    .show()

        }
    }

    private fun openItem(id: String?, view: View){
        val intent = Intent(activity as MainActivity, ScheduleActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity as MainActivity, view, "openItem").toBundle())
    }

    private fun createDialog(): View {
        val layout = LinearLayout(ContextThemeWrapper(activity?.applicationContext, R.style.AppTheme))
        layout.layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        layout.setPadding(dip(16), dip(16), dip(16), dip(16))
        layout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(matchParent, matchParent)
        params.topMargin = 16

        val nameTil = TextInputLayout(ContextThemeWrapper(activity?.applicationContext, R.style.AppTheme))
        nameTil.layoutParams = params
        val nameEditText = TextInputEditText(ContextThemeWrapper(activity?.applicationContext, R.style.AppTheme))
        nameEditText.layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
        nameEditText.hint = "行程名稱"
        nameEditText.setSingleLine(true)
        nameTil.addView(nameEditText)
        layout.addView(nameTil)

        val myCalendar = Calendar.getInstance()


        val dateEditText = AppCompatEditText(ContextThemeWrapper(activity?.applicationContext, R.style.AppTheme))
        dateEditText.layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
        dateEditText.hint = "行程開始日期"
        dateEditText.isClickable = true
        dateEditText.isFocusable = false
        dateEditText.setSingleLine(true)
        dateEditText.setOnClickListener {
            DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy/MM/dd" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

                dateEditText.setText(sdf.format(myCalendar.time))
            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        layout.addView(dateEditText)

        return layout
    }


    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
        realm = null
    }

}// Required empty public constructor
