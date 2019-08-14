package chawan.fame.editerbook

import android.app.Application
import androidx.room.Room
import co.fictionlog.fictionlog.data.local.database.EditerDatabase

class EditorBookApplication : Application() {

    companion object {
        var database: EditerDatabase? = null
    }

    fun createDb() {
        database = Room.databaseBuilder(this, EditerDatabase::class.java, "editer-db")
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Contextor.setContext(this)
        database = Room.databaseBuilder(this, EditerDatabase::class.java, "editer-db")
            .fallbackToDestructiveMigration()
            .build()

    }
}