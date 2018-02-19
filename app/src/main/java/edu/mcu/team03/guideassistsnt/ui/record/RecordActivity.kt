package edu.mcu.team03.guideassistsnt.ui.record

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Gravity
import android.view.Menu
import android.view.View
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Record
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_record.*
import org.jetbrains.anko.startActivity




class RecordActivity : AppCompatActivity() {

    private var realm: Realm? = null
    private var adapter: RecordAdapter? = null
    private var type : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        setSupportActionBar(toolbar)

        val bundle = this.intent.extras

        title = bundle.getString("type")
        type = bundle.getString("type")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab.setOnClickListener { _ ->
            startActivity<EditRecordActivity>("action" to "add","type" to type)
        }

        realm = Realm.getDefaultInstance()

        setupList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_record, menu)
        val menuSearchItem = menu.findItem(R.id.action_search)

        val searchView = menuSearchItem.actionView as SearchView

        searchView.setPadding(0,0,0,0)
        searchView.gravity = Gravity.START
        searchView.isSubmitButtonEnabled = false

        val itemClick = this::openItem

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val realmQuery = realm!!.where<Record>().equalTo("type", type)
                        .beginGroup()
                        .contains("title", newText).or().contains("telephone", newText)
                        .or()
                        .contains("website", newText).or().contains("content", newText)
                        .or()
                        .contains("county", newText).or().contains("address", newText)
                        .endGroup()
                        .findAll()
                adapter = RecordAdapter(realmQuery, itemClick)
                record_list.adapter = adapter
                return false
            }
        })

        // 這邊讓icon可以還原到搜尋的icon
        searchView.setIconifiedByDefault(true)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupList() {
        adapter = RecordAdapter(realm!!.where<Record>().equalTo("type",type).findAll(), this::openItem)
        record_list.layoutManager = LinearLayoutManager(this)
        record_list.adapter = adapter
        record_list.setHasFixedSize(true)
    }

    private fun openItem(id: String?, view: View, long: Boolean){
        val intent = Intent(this, ViewRecordActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "openItem").toBundle())
//        startActivity<ViewRecordActivity>("id" to id)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }
}
