package edu.mcu.team03.guideassistsnt.ui.record

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Patterns
import android.view.*
import android.widget.ArrayAdapter
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Image
import edu.mcu.team03.guideassistsnt.model.Record
import id.zelory.compressor.Compressor
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_record_edit.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class EditRecordActivity : AppCompatActivity() {

    private var realm: Realm? = null
    private var saveImage: ArrayList<Image> = ArrayList()
    private var chooseImage: ArrayList<Image> = ArrayList()
    private val pageList = ArrayList<View>()
    private var action: String? = null
    private var type : String? = null
    private var id: String? = null
    private var delImage: ArrayList<Image> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realm = Realm.getDefaultInstance()

        val bundle = this.intent.extras
        action = bundle.getString("action")
        type = bundle.getString("type")

        telEditText.addTextChangedListener(PhoneNumberFormattingTextWatcher("TW"))
        val arrayAdapter = ArrayAdapter(applicationContext, R.layout.spinner_item, resources.getStringArray(R.array.taiwan_list))
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adrSpinner.adapter = arrayAdapter

        if (action == "add"){
            collapsing_toolbar.title = "新增紀錄"

            updateViewPager()
        } else {
            id = bundle.getString("id")
            collapsing_toolbar.title = "修改紀錄"
            val records = realm?.where<Record>()?.equalTo("id", id)?.findFirst()
            if (!records?.images!!.isEmpty()){
                records.images.forEach {
                    saveImage.add(it)
                }
            }

            updateViewPager()

            titleEditText.setText(records.title)
            telEditText.setText(records.telephone)
            adrSpinner.setSelection(resources.getStringArray(R.array.taiwan_list).indexOf(records.county))
            adrEditText.setText(records.address)
            websiteEditText.setText(records.website)
            rating.rating = records.rating?.toFloat() ?: 0f
            contentEditText.setText(records.content)
        }


    }

    private fun addImage(){
        val widget = Widget.newDarkBuilder(applicationContext).statusBarColor(ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark)).toolBarColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary)).title("選擇圖片").build()
        Album.image(this) // Image selection.
                .multipleChoice()
                .widget(widget)
                .requestCode(200)
                .camera(false)
                .columnCount(2)
                .selectCount(6)
                .onResult({ _, result ->
                    for (i in 0 until result.size) {
                        val image = Image()
                        image.path = result[i].path
                        chooseImage.add(image)
                    }
                    updateViewPager()
                })
                .start()
    }

    private fun delImage(save: Boolean, image: Image){
        if(save) {
            delImage.add(image)
            saveImage.remove(image)
            updateViewPager()
        } else {
            chooseImage.remove(image)
            updateViewPager()
        }
    }

    private fun updateViewPager() {
        pageList.clear()
        (0 until saveImage.size).mapTo(pageList) { ImageEditView(ContextThemeWrapper(applicationContext, R.style.AppTheme), saveImage[it], true, this::delImage) }
        (0 until chooseImage.size).mapTo(pageList) { ImageEditView(ContextThemeWrapper(applicationContext, R.style.AppTheme), chooseImage[it], false, this::delImage) }
        pageList.add(ImageAddView(ContextThemeWrapper(applicationContext, R.style.AppTheme), this::addImage))
        imageViewPager.adapter = ImageViewPagerAdapter(pageList)
        indicator.setViewPager(imageViewPager)
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
                saveData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveData() {
        if (inputCheck()) {
            val saveTempImage = ArrayList<Image>()
            val loading =  SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            loading.progressHelper.barColor = Color.parseColor("#A5DC86")
            loading.titleText = "儲存中"
            loading.setCancelable(false)
            loading.show()

            if (action != "add") {
                saveImage.forEach {
                    saveTempImage.add(it)
                }
                delImage.forEach {
                    val file = File(it.path)
                    doAsync { if (file.exists()) file.delete() }
                    realm?.executeTransaction { _ ->
                        it.deleteFromRealm()
                    }
                }
            }

            doAsync {
                chooseImage.forEach {
                    val file = File(it.path)
                    val compressedImage = Compressor(applicationContext)
                            .setDestinationDirectoryPath(filesDir.absolutePath + "/images")
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToFile(file)
                    val renameFile = File(compressedImage.parentFile, UUID.randomUUID().toString() + "." + compressedImage.extension)
                    compressedImage.renameTo(renameFile)
                    val tempImage = Image()
                    tempImage.path = renameFile.path
                    saveTempImage.add(tempImage)
                }
                uiThread {
                    realm?.executeTransaction {
                        var record: Record? = if (action == "add") {
                            realm?.createObject(Record::class.java, UUID.randomUUID().toString())
                        } else {
                            realm?.where<Record>()?.equalTo("id", id)?.findFirst()
                        }
                        record?.title = titleEditText.text.toString()
                        record?.type = type ?: "其他"
                        record?.telephone = telEditText.text.toString()
                        record?.website = websiteEditText.text.toString()
                        record?.address = adrEditText.text.toString()
                        record?.content = contentEditText.text.toString()
                        record?.county = adrSpinner.selectedItem.toString()
                        record?.address = adrEditText.text.toString()
                        record?.rating = rating.rating.toDouble()
                        record?.images?.clear()
                        saveTempImage.forEach {
                            val tempImage = realm?.copyToRealm(it)
                            record?.images?.add(tempImage)
                        }
                    }
                    loading.dismiss()
                    finish()
                }
            }

        }
    }

    private fun inputCheck(): Boolean{
        inputForm.forEachChild {
            if (it is TextInputLayout) {
                it.error = ""
                it.isErrorEnabled = false
            } else if (it is ViewGroup) {
                it.forEachChild {
                    if (it is TextInputLayout) {
                        it.error = ""
                        it.isErrorEnabled = false
                    }
                }
            }
        }
        var check = true
        if (titleEditText.text.toString() == "") {
            titleTil.error = "請填寫內容"
            titleTil.isErrorEnabled = true
            check = false
        }
        if (telEditText.text.toString() != ""&& !Patterns.PHONE.matcher(telEditText.text.toString()).matches()) {
            telTil.error = "電話號碼格式不正確"
            telTil.isErrorEnabled = true
            check = false
        }
        if (adrEditText.text.toString() != ""&& adrSpinner.selectedItem.toString() == "無") {
            adrTil.error = "請輸入縣市"
            adrTil.isErrorEnabled = true
            check = false
        }
        if (websiteEditText.text.toString() != "" && !Patterns.WEB_URL.matcher(websiteEditText.text.toString()).matches()) {
            websiteTil.error = "請輸入正確的網址"
            websiteTil.isErrorEnabled = true
            check = false
        }

        return check
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }
}
