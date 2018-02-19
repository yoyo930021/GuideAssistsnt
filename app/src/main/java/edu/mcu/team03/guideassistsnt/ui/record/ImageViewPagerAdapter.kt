package edu.mcu.team03.guideassistsnt.ui.record

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup


/**
 * Created by yoyo930021 on 2018/1/5.
 */
class ImageViewPagerAdapter(mListViews: ArrayList<View>): PagerAdapter() {
    private var mListViews: ArrayList<View> = ArrayList()

    init {
        this.mListViews = mListViews
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = mListViews[position]
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return mListViews.size
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }
}