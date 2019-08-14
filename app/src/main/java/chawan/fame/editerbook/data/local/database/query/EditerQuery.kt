package co.fictionlog.fictionlog.data.local.database.query

import androidx.room.*
import androidx.room.Delete
import co.fictionlog.fictionlog.data.local.database.table.EditerTable
import io.reactivex.Single


/**
 * Created by fame on 7/2/2018 AD.
 */


@Dao
interface EditerQuery {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContent(user: EditerTable)

    @Delete
    fun deleteAll(user: EditerTable)

    @Query("SELECT * FROM editer_table WHERE bookId = :bookId")
    fun getContent(bookId: Int): Single<List<EditerTable>>
}