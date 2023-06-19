package com.stevedenheyer.scriptassistant.audioeditor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.SentencesCollection
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import com.stevedenheyer.scriptassistant.views.WaveformUniverseView
import android.util.Range


class WaveformsUniverseAdapter : RecyclerView.Adapter<WaveformsUniverseAdapter.ViewHolder>(){

    private val waveformDiff = AsyncListDiffer(this, WfmDiffCallback())

    private val sentencesMap = HashMap<Long, SentencesCollection>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val waveformView = view.findViewById<WaveformUniverseView>(R.id.WaveForm)

        fun bind(waveform: Waveform) {
            if (waveformView.getWaveformSize() != waveform.data.size) {
                waveformView.setWaveform(waveform.data)
            }

            waveformView.setLoadingState(waveform.isLoading)

            waveformView.setAudioRange(Range(0, waveform.data.size))

        }

        fun bind(sentencesCollection: SentencesCollection) {
        //    Log.d("ADPT", "Binding sentences...")
            waveformView.setSentanceMarkers(sentencesCollection.data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.waveform_universe_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val waveformData = waveformDiff.currentList.get(position)
        val sentences = sentencesMap[getItemId(position)] ?: SentencesCollection(-1, emptyArray())
        holder.bind(waveformData)
        holder.bind(sentences)
    }

    override fun getItemCount(): Int {
        return waveformDiff.currentList.size
    }

    override fun getItemId(position: Int): Long {

        return waveformDiff.currentList[position].id
    }


    fun updateWaveform(data: Array<Waveform>?) {
        if (data != null) {
            waveformDiff.submitList(data.asList())
        }
           // Log.d("ADPT", "Adapter updated: ${itemCount}")
    }

    fun updateSentences(map: Map<Long, SentencesCollection>?) {
      //  Log.d("ADPT", "update sentences ${map?.size}")
        if (map != null) {
            sentencesMap.putAll(map)
            notifyDataSetChanged()
        }
    }

    private class WfmDiffCallback: ItemCallback<Waveform>() {
        override fun areItemsTheSame(oldItem: Waveform, newItem: Waveform): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Waveform, newItem: Waveform): Boolean {
            return oldItem.data.size == newItem.data.size && oldItem.isLoading == newItem.isLoading
        }

    }
}