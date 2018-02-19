package edu.mcu.team03.guideassistsnt.util

import android.webkit.URLUtil
import android.widget.ImageView
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader
import com.yanzhenjie.album.task.DefaultAlbumLoader
import edu.mcu.team03.guideassistsnt.GlideApp
import java.io.File


/**
 * Created by yoyo930021 on 2018/1/5.
 */
class GlideAlbumLoader : AlbumLoader {

    override fun loadAlbumFile(imageView: ImageView, albumFile: AlbumFile, viewWidth: Int, viewHeight: Int) {
        val mediaType = albumFile.mediaType
        if (mediaType == AlbumFile.TYPE_IMAGE) {
            GlideApp.with(imageView.context)
                    .load(albumFile.path)
                    .into(imageView)
        } else if (mediaType == AlbumFile.TYPE_VIDEO) {
            DefaultAlbumLoader.getInstance()
                    .loadAlbumFile(imageView, albumFile, viewWidth, viewHeight)
        }
    }

    override fun loadImage(imageView: ImageView, imagePath: String, width: Int, height: Int) {
        if (URLUtil.isNetworkUrl(imagePath)) {
            GlideApp.with(imageView.context)
                    .load(imagePath)
                    .into(imageView)
        } else {
            GlideApp.with(imageView.context)
                    .load(File(imagePath))
                    .into(imageView)
        }
    }

}