package edu.mcu.team03.guideassistsnt.ui.record

import android.content.Context
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.AppCompatImageView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import edu.mcu.team03.guideassistsnt.GlideApp
import edu.mcu.team03.guideassistsnt.R
import edu.mcu.team03.guideassistsnt.model.Image
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent


/**
 * Created by yoyo930021 on 2018/1/5.
 */
class ImageEditView(context: Context, image: Image, save: Boolean,  doing: (save: Boolean, image: Image) -> Unit) : FrameLayout(context.applicationContext) {

    init {
        val layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        this.layoutParams = layoutParams

        val imageView = AppCompatImageView(context)
        GlideApp.with(context).load(image.path).centerCrop().into(imageView)
        val imageParams = FrameLayout.LayoutParams(matchParent, matchParent)
        imageParams.gravity = Gravity.CENTER
        imageView.layoutParams = imageParams
        addView(imageView)

        val imageButton = AppCompatImageButton(context)
        imageButton.background = context.getDrawable(R.drawable.circle_transparent_background)
        imageButton.imageResource = R.drawable.ic_delete
        val buttonParams = FrameLayout.LayoutParams(dip(50), dip(50))
        buttonParams.gravity = Gravity.CENTER
        imageButton.layoutParams = buttonParams
        imageButton.setOnClickListener { doing(save,image) }
        addView(imageButton)

    }

}