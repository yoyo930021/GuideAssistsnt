package edu.mcu.team03.guideassistsnt.ui.billing

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatSpinner
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Billing
import edu.mcu.team03.guideassistsnt.model.NumUnit
import edu.mcu.team03.guideassistsnt.model.Schedule
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_billing_edit.*
import org.jetbrains.anko.*
import java.util.*


class EditBillingActivity : AppCompatActivity() {

    private var realm: Realm? = null
    private var action: String? = null
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing_edit)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realm = Realm.getDefaultInstance()

        val bundle = this.intent.extras
        action = bundle.getString("action")
        id = bundle.getString("id")

        when (action) {
            "add" -> {
                title = "新增記帳"
                numList.addView(makeNumUnit())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_record_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.action_check -> {
                if (inputCheck()) {
                    realm?.executeTransaction { realm ->
                        val schedule = realm.where<Schedule>().equalTo("id", id).findFirst()
                        val billing = realm.createObject<Billing>(UUID.randomUUID().toString())
                        billing.title = titleEditText.text.toString()
                        billing.type = typeSpinner.selectedItem.toString()
                        var sum = 0
                        numList.forEachChild {
                            val numUnit = realm.createObject<NumUnit>()
                            numUnit.num = ((it as LinearLayout).getChildAt(0) as AppCompatEditText).text.toString().toInt()
                            numUnit.unit = ((it).getChildAt(1) as AppCompatSpinner).selectedItem.toString()
                            sum += numUnit.num
                            billing.nums.add(numUnit)
                        }
                        billing.sum = sum
                        billing.content = contentEditText.text.toString()
                        schedule?.billngs?.add(billing)
                        finish()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun inputCheck(): Boolean{
        inputForm.forEachChild {
            if (it is TextInputLayout) {
                it.error = ""
                it.isErrorEnabled = false
            }
        }

        var check = true
        if (titleEditText.text.toString() == "") {
            titleTil.error = "請輸入帳款名稱"
            titleTil.isErrorEnabled = true
            check = false
        }


        numList.forEachChild {
            if (it is LinearLayout) {
                if ((it.getChildAt(0) as AppCompatEditText).text.toString() == "") {
                    check = false
                }
            }
        }

        return check
    }


    private fun makeNumUnit(): View {
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        val params = LinearLayout.LayoutParams(matchParent, wrapContent)
        linearLayout.layoutParams = params
        val numEditText = AppCompatEditText(this)
        numEditText.hint = "數字"
        numEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        val weightParams = LinearLayout.LayoutParams(0, wrapContent, 3f)
        numEditText.layoutParams = weightParams
        linearLayout.addView(numEditText)
        val unitSpinner = AppCompatSpinner(this, Spinner.MODE_DIALOG)
        val unitList = ArrayAdapter.createFromResource(this,
                R.array.unit_list,
                android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = unitList
        unitSpinner.layoutParams = weightParams
        linearLayout.addView(unitSpinner)
        val addButton = ImageButton(this)
        addButton.imageResource = R.drawable.ic_add_black
        addButton.backgroundColor = Color.TRANSPARENT
        val centerParams = LinearLayout.LayoutParams(0, wrapContent, 2f)
        centerParams.gravity = Gravity.CENTER_VERTICAL
        addButton.layoutParams = centerParams
        addButton.isClickable = true
        addButton.setOnClickListener {
            Log.d("Debug", "click")
            numList.addView(makeNumUnit())
        }
        linearLayout.addView(addButton)
        return linearLayout
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }

}
