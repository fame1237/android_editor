package chawan.fame.editerbook

import android.app.Application

class EditerBookApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Contextor.setContext(this)
    }

}