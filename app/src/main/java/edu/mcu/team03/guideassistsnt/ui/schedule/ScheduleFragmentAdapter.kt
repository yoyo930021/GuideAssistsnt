package edu.mcu.team03.guideassistsnt.ui.schedule

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by yoyo930021 on 2018/1/11.
 */
class ScheduleFragmentAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {
    private val mFragmentList: ArrayList<Fragment> = arrayListOf()
    private val mFragmentTitleList: ArrayList<String> = arrayListOf()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position]
    }
}