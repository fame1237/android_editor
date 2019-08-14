package co.fictionlog.fictionlog.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import co.fictionlog.fictionlog.data.local.database.query.*
import co.fictionlog.fictionlog.data.local.database.table.EditerTable

/**
 * Created by fame on 7/2/2018 AD.
 */

@Database(
    entities = [(EditerTable::class)],
    version = 1, exportSchema = true
)
abstract class EditerDatabase : RoomDatabase() {
    abstract fun editerQuery(): EditerQuery
}