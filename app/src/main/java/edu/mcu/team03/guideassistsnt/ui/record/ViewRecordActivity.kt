package edu.mcu.team03.guideassistsnt.ui.record

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.view.*
import edu.mcu.team03.guideassistsnt.GlideApp
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Image
import edu.mcu.team03.guideassistsnt.model.Record
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_record_view.*
import org.jetbrains.anko.*
import java.io.File

private const val REQUEST_PHONE_CALL = 1

class ViewRecordActivity : AppCompatActivity() {

    private var realm: Realm? = null
    private var id: String? = null
    private var type: String? = null
    private val pageList = ArrayList<View>()
    private var telephone: String? = null
    private var readonly: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realm = Realm.getDefaultInstance()
        val bundle = this.intent.extras
        id = bundle.getString("id")
        readonly = bundle.getBoolean("readonly")

        val records = realm?.where<Record>()?.equalTo("id", id)?.findFirst()
        initView(records)
    }

    private fun initView(records: Record?) {
        pageList.clear()
        if (!records?.images!!.isEmpty()){
            records.images.forEach {
                pageList.add(makeImageView(it))
            }
        }

        type = records.type

        imageViewPager.adapter = ImageViewPagerAdapter(pageList)
        indicator.setViewPager(imageViewPager)

        collapsing_toolbar.title = records.title
        telTextView.text = if (records.telephone!="") records.telephone else "無"
        if (records.telephone!="") {
            telTil.setOnClickListener {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    telephone = records.telephone!!.replace(" ","")
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
                }
                else { makeCall(records.telephone!!.replace(" ","")) }
            }
        }
        adrTextView.text = records.county+records.address
        if (records.address!="") {
            adrTil.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:0,0?q="+records.county+records.address)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.`package` = "com.google.android.apps.maps"
                startActivity(mapIntent)
            }
        }
        websiteTextView.text = if (records.website!="") records.website else "無"
        if (records.website!="") {
            websiteTil.setOnClickListener { browse(records.website!!) }
        }
        rating.rating = records.rating?.toFloat() ?: 0f
        contentTextView.text = if (records.content!="") records.content else "無"
    }

    private fun makeImageView(image: Image): AppCompatImageView{
        val imageView = AppCompatImageView(ContextThemeWrapper(applicationContext,R.style.AppTheme))
        GlideApp.with(applicationContext).load(image.path).centerCrop().into(imageView)
        val imageParams = ViewGroup.LayoutParams(matchParent, matchParent)
        imageView.layoutParams = imageParams
        return imageView
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        if (readonly) {
            inflater.inflate(R.menu.activity_record_view_readonly, menu)
        } else {
            inflater.inflate(R.menu.activity_record_view, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.action_edit -> {
                startActivity<EditRecordActivity>("action" to "edit", "id" to id, "type" to type)
                true
            }
            R.id.action_del -> {
                AlertDialog.Builder(this)
                        .setTitle("你確定")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("你確定要刪除這個紀錄嗎？")
                        .setCancelable(true)
                        .setPositiveButton("確定", { _, _ ->
                            val records = realm?.where<Record>()?.equalTo("id", id)?.findFirst()
                            realm?.executeTransaction {
                                if (records?.images!!.isEmpty().not()) {
                                    records.images.createSnapshot().forEach {
                                        val file = File(it.path)
                                        doAsync { if (file.exists()) file.delete() }
                                        it.deleteFromRealm()
                                    }
                                }
                                records.images.clear()
                                records.deleteFromRealm()
                                finish()
                            }
                        })
                        .setNegativeButton("取消", {dialog, _ -> dialog.cancel() })
                        .show()
                true
            }
            R.id.action_share -> {
                val records = realm?.where<Record>()?.equalTo("id", id)?.findFirst()
                val chooseDialog = AlertDialog.Builder(this)
                chooseDialog.setTitle("選擇方享項目")
                val listChoose: Array<String>? = if (records?.images!!.isEmpty().not()) {
                    arrayOf("只分享文字","只分享圖片","分享圖片與文字")
                } else {
                    arrayOf("只分享文字")
                }
                chooseDialog.setItems(listChoose, { _, which ->
                    val snackbar = Snackbar.make(this.contentView!!, "準備分享中", Snackbar.LENGTH_INDEFINITE)
                    snackbar.show()
                    when (which) {
                        0 -> {
                            val temp = StringBuilder()
                            temp.append("私～導遊的秘密 分享：")
                            temp.append(records.title + "\n")
                            if (records.telephone != "") temp.append("電話：" + records.telephone + "\n")
                            if (records.website != "") temp.append("網站：" + records.website + "\n")
                            if (records.address != "") temp.append("地址：" + records.county + records.address + "\n")
                            if (records.content != "") temp.append(records.content + "\n")
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_SUBJECT, records.title)
                            intent.putExtra(Intent.EXTRA_TEXT, temp.toString())
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            snackbar.dismiss()
                            startActivity(Intent.createChooser(intent, "分享"))
                        }
                        1 -> {
                            val imageFiles = ArrayList<File>()
                            val imageUris = ArrayList<Uri>()
                            records.images.forEach {
                                imageFiles.add(File(it.path))
                            }
                            doAsync {
                                imageFiles.forEach {
                                    val cpFile = File(applicationContext.externalCacheDir, it.name)
                                    if (!cpFile.exists()) it.copyTo(cpFile, false)
                                    imageUris.add(FileProvider.getUriForFile(applicationContext, packageName + ".sharephoto.provider", cpFile))
                                }
                                uiThread {
                                    snackbar.dismiss()
                                    val shareIntent = Intent()
                                    shareIntent.action = Intent.ACTION_SEND_MULTIPLE
                                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
                                    shareIntent.type = "image/*"
                                    startActivity(Intent.createChooser(shareIntent, "圖片分享"))
                                }
                            }
                        }
                        2 -> {
                            val temp = StringBuilder()
                            temp.append("私～導遊的秘密 分享：")
                            temp.append(records.title + "\n")
                            if (records.telephone != "") temp.append("電話：" + records.telephone + "\n")
                            if (records.website != "") temp.append("網站：" + records.website + "\n")
                            if (records.address != "") temp.append("地址：" + records.county + records.address + "\n")
                            if (records.content != "") temp.append(records.content + "\n")
                            val imageFiles = ArrayList<File>()
                            val imageUris = ArrayList<Uri>()
                            records.images.forEach {
                                imageFiles.add(File(it.path))
                            }
                            doAsync {
                                val cpFile = File(applicationContext.externalCacheDir, imageFiles[0].name)
                                if (!cpFile.exists()) imageFiles[0].copyTo(cpFile, false)
                                imageUris.add(FileProvider.getUriForFile(applicationContext, packageName + ".sharephoto.provider", cpFile))

                                uiThread {
                                    snackbar.dismiss()
                                    val shareIntent = Intent()
                                    shareIntent.action = Intent.ACTION_SEND
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUris[0])
                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, records.title)
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, temp.toString())
                                    shareIntent.type = "image/*"
                                    startActivity(Intent.createChooser(shareIntent, "分享"))
                                }
                            }
                        }
                    }
                })
                chooseDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        val records = realm?.where<Record>()?.equalTo("id", id)?.findFirst()
        initView(records)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PHONE_CALL -> { makeCall(telephone!!) }
        }
    }

}
