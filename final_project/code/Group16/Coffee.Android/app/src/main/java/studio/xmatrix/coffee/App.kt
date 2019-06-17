package studio.xmatrix.coffee

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import studio.xmatrix.coffee.data.model.MyObjectBox
import studio.xmatrix.coffee.data.store.DefaultSharedPref
import studio.xmatrix.coffee.ui.NightModeConfig
import studio.xmatrix.coffee.inject.AppInjector
import timber.log.Timber


class App : Application() {

    lateinit var boxStore: BoxStore
        private set

    override fun onCreate() {
        super.onCreate()

        // 设置夜间模式
        val isNightAuto = NightModeConfig.getInstance().isNightAuto(this)
        if (!isNightAuto) {
            val isNightMode = NightModeConfig.getInstance().getNightMode(this)
            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        }


        boxStore = MyObjectBox.builder().androidContext(this).build()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            AndroidObjectBrowser(boxStore).start(this)
        }

        DefaultSharedPref.init(this)
        AppInjector.init(this)
    }
}
