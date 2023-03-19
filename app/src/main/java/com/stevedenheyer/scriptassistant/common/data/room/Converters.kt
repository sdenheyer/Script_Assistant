package com.stevedenheyer.scriptassistant.common.data.room

import android.util.Range
import androidx.room.TypeConverter
import com.stevedenheyer.scriptassistant.common.data.room.model.LineDB
import com.stevedenheyer.scriptassistant.common.data.room.model.SentenceDB
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromSentanceList(sentenceDBS: Array<SentenceDB>): String {
        val jsonArray = JSONArray()
        sentenceDBS.forEach { sentance ->
            val jsonObject = JSONObject()
            jsonObject.put("begin", sentance.begin)
            jsonObject.put("end", sentance.end)
            jsonObject.put("line_id", sentance.lineId)
            jsonObject.put("take", sentance.take)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toSentanceList(jsonString: String): Array<SentenceDB> {
        val sentances = ArrayList<SentenceDB>()
        val json = JSONArray(jsonString)
        val length = json.length()
        if (length == 0) {
            return emptyArray()
        }
        for (i in 0..(length - 1)) {
            val jsonObject = json.get(i) as JSONObject
            val sentance = SentenceDB(
                jsonObject.optInt("begin"),
                jsonObject.optInt("end"),
                jsonObject.optLong("line_id"),
                jsonObject.optInt("take")
            )
            sentances.add(sentance)
        }
        return sentances.toTypedArray()
    }
/*
    @TypeConverter
    fun fromLines(script: Array<LineDB>): String {
        val jsonArray = JSONArray()
        script.forEach { line ->
            val jsonObject = JSONObject()
            jsonObject.put("id", line.id)
            jsonObject.put("text", line.text)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toLines(jsonString: String): Array<LineDB> {
        val lines = ArrayList<LineDB>()
        val json = JSONArray(jsonString)
        val length = json.length()
        if (length == 0) {
            return emptyArray()
        }
        for (i in 0..(length - 1)) {
            val jsonObject = json.get(i) as JSONObject
            val line = LineDB(
                jsonObject.optLong("id"),
                jsonObject.optString("text")
            )
            lines.add(line)
        }
        return lines.toTypedArray()
    }*/
}