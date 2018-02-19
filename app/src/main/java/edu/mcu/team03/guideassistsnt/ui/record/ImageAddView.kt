package edu.mcu.team03.guideassistsnt.ui.record

import android.content.Context
import android.support.v7.widget.AppCompatImageButton
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import edu.mcu.team03.guideassistsnt.R
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent


/**
 * Created by yoyo930021 on 2018/1/5.
 */
class ImageAddView(context: Context, doing: () -> Unit): FrameLayout(context) {
    init {
        val layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        this.layoutParams = layoutParams
        background = context.getDrawable(R.color.colorPrimary)

        val imageButton = AppCompatImageButton(context)
        imageButton.background = context.getDrawable(R.drawable.circle_transparent_background)
        imageButton.imageResource = R.drawable.ic_add
        val outValue = TypedValue()
        getContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        val buttonParams = FrameLayout.LayoutParams(dip(50), dip(50))
        buttonParams.gravity = Gravity.CENTER
        imageButton.layoutParams = buttonParams
        imageButton.setOnClickListener { doing() }
        addView(imageButton)
    }
}