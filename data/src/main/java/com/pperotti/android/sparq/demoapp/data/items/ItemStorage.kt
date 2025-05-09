package com.pperotti.android.sparq.demoapp.data.items

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<StorageItem>)

    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<StorageItem>

    @Query("SELECT COUNT(*) FROM items")
    suspend fun getItemCount(): Int

    @Query("DELETE FROM items")
    suspend fun deleteAllItems()
}

@Database(
    entities = [
        StorageItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}

@Entity(tableName = "items")
data class StorageItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String?,
    val description: String?
)

/**
 * Mapper function between StorageItem and Item
 */
fun StorageItem.toItem(): Item {
    return Item(
        id = id,
        title = title,
        description = description
    )
}

/**
 * Mapper Function between RemoteItem and StorageItem
 */
fun RemoteItem.toStorageItem(): StorageItem {
    return StorageItem(
        id = 0,
        title = title,
        description = description
    )
}
