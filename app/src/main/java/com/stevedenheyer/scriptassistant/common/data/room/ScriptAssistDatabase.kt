package com.stevedenheyer.scriptassistant.common.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stevedenheyer.scriptassistant.common.data.room.daos.ProjectDao
import com.stevedenheyer.scriptassistant.common.data.room.daos.AudioDetailsDao
import com.stevedenheyer.scriptassistant.common.data.room.daos.AudioFileDao
import com.stevedenheyer.scriptassistant.common.data.room.model.LineDB
import com.stevedenheyer.scriptassistant.common.data.room.model.ProjectDB
import com.stevedenheyer.scriptassistant.common.data.room.model.ScriptDB
import com.stevedenheyer.scriptassistant.data.*

@Database(entities = [ProjectDB::class, ScriptDB::class, LineDB::class, AudioFileDB::class, AudioDetailsDB::class, ProjectAudiofilesCrossRef::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ScriptAssistDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun audioFileDao(): AudioFileDao
    abstract fun audioDetailsDao(): AudioDetailsDao

    companion object {
        @Volatile private var instance: ScriptAssistDatabase? = null

        fun getInstance(context: Context): ScriptAssistDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ScriptAssistDatabase {
            return Room.databaseBuilder(context, ScriptAssistDatabase::class.java, "voassist-db")
             .build()
        }
    }
}