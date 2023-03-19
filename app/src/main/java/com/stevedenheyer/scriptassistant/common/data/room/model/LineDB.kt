package com.stevedenheyer.scriptassistant.common.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stevedenheyer.scriptassistant.common.domain.model.script.Line

@Entity
data class LineDB(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val scriptOwnerId: Long,
    val index: Int,
    val text: String
) {
    companion object {
        fun fromDomain(line: Line) = LineDB(line.id, line.scriptOwnerId, line.index, line.text)
    }

    fun toDomain() = Line(id, scriptOwnerId, index, text)
}
