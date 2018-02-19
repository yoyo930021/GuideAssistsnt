package edu.mcu.team03.guideassistsnt.ui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.view.MenuItem
import android.view.ViewGroup
import edu.mcu.team03.guideassistsnt.GlideApp
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.ui.billing.BillingFragment
import edu.mcu.team03.guideassistsnt.ui.now.NowFragment
import edu.mcu.team03.guideassistsnt.ui.record.RecordFragment
import edu.mcu.team03.guideassistsnt.ui.schedule.ScheduleFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

const val NAV_STATE  = "main_nav_state"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

//    private var prefs:SharedPreferences? = null
    private var navState = R.id.nav_now

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        val circleImageView = (nav_view.getHeaderView(0) as ViewGroup).getChildAt(0) as AppCompatImageView
        GlideApp.with(applicationContext).load(R.mipmap.teacher).circleCrop().into(circleImageView)

        nav_view.setNavigationItemSelectedListener(this)

        // Set to default page
        savedInstanceState?.let { navState = savedInstanceState.getInt(NAV_STATE) }
        nav_view.setCheckedItem(navState)
        switchDisplayFragment(nav_view.menu.findItem(navState))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(NAV_STATE, navState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        switchDisplayFragment(item)

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun switchDisplayFragment(item: MenuItem) {
        var fragment: Fragment? = null

        when (item.itemId){
            R.id.nav_now -> { fragment = NowFragment() }
            R.id.nav_record -> { fragment = RecordFragment() }
            R.id.nav_schedule -> { fragment = ScheduleFragment() }
            R.id.nav_bill -> { fragment = BillingFragment() }
//            R.id.nav_setting -> { fragment = SettingFragment() }
        }

        title = item.title

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_layout, fragment)
        ft.commit()

        navState = item.itemId
    }

}
