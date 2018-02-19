package edu.mcu.team03.guideassistsnt.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Record
import edu.mcu.team03.guideassistsnt.ui.record.RecordAdapter
import edu.mcu.team03.guideassistsnt.ui.record.ViewRecordActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_searchable.*


class SearchableActivity : AppCompatActivity() {

    private var query:String? = null
    private var action:String? = null
    private var type:String? = null
    private var realm: Realm? = null
    private var adapter: RecordAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realm = Realm.getDefaultInstance()

        if (Intent.ACTION_SEARCH == intent.action) {
            query = intent.getStringExtra(SearchManager.QUERY)
            val appData = intent.getBundleExtra(SearchManager.APP_DATA)
            appData?.let {
                action = appData.getString("action")
                if (action =="search") {
                    type = appData.getString("type")
                    initSearchView()
                }
            }
        } else if(intent.extras.getString("action")=="choose"){

        }

    }

    private fun initSearchView(){
        title = query
        val realmQuery = realm!!.where<Record>().equalTo("type", type)
                .beginGroup()
                .contains("title", query!!).or().contains("telephone", query!!)
                .or()
                .contains("website", query!!).or().contains("content", query!!)
                .or()
                .contains("county", query!!).or().contains("address", query!!)
                .endGroup()
                .findAll()
        adapter = RecordAdapter(realmQuery, this::openItem)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        list.setHasFixedSize(true)
    }

    private fun openItem(id: String?, view: View, long: Boolean){
        val intent = Intent(this, ViewRecordActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "openItem").toBundle())
//        startActivity<ViewRecordActivity>("id" to id)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }
}
