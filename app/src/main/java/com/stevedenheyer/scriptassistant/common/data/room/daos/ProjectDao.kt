package com.stevedenheyer.scriptassistant.common.data.room.daos

import androidx.room.*
import com.stevedenheyer.scriptassistant.common.data.room.model.*
import kotlinx.coroutines.flow.*

@Dao
interface ProjectDao {

    @Insert
    suspend fun insertProject(projectDB: ProjectDB) : Long

    @Insert
    suspend fun insertScript(scriptDB: ScriptDB) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScriptLine(lineDB: LineDB) : Long

    @Delete
    suspend fun delete(projectDB: ProjectDB)

    @Query("SELECT * FROM projectDB WHERE :id IS projectId")
    fun getProjectById(id: Long?) : Flow<ProjectDB>

    @Transaction
    @Query("SELECT * FROM projectdb WHERE :id IS projectId")
    suspend fun getProjectAndScript(id: Long?) : ProjectAndScript

    @Transaction
    @Query("SELECT * FROM scriptdb WHERE :id IS scriptId")
    fun getScriptWithLines(id: Long?) : Flow<ScriptWithLines>

    @Query("SELECT * FROM projectDB")
    fun getAll(): Flow<List<ProjectDB>>

    @Update
    suspend fun updateProject(projectDB: ProjectDB)

    @Update
    suspend fun updateScript(scriptDB: ScriptDB)

    @Update
    suspend fun updateScriptLine(LineDB: LineDB): Int
}