package edu.mcu.team03.guideassistsnt

import android.app.Application
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import edu.mcu.team03.guideassistsnt.util.GlideAlbumLoader
import io.realm.Realm
import io.realm.RealmConfiguration


/**
 * Created by yoyo930021 on 2017/12/18.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(applicationContext)

        val realmConfiguration = RealmConfiguration.Builder()
                .name("default.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(realmConfiguration)

        Album.initialize(
                AlbumConfig.newBuilder(this)
                        .setAlbumLoader(GlideAlbumLoader()) // Set album load.
                        .build()
        )
    }

}