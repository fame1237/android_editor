package co.fictionlog.fictionlog.data.local.database.table

import androidx.room.Entity
import androidx.room.Ignore

/**
 * Created by fame on 7/2/2018 AD.
 */


@Entity(tableName = "editer_table", primaryKeys = ["bookId"])
data class EditerTable(
    var bookId: Int = 0,
    var content: String = ""
)
