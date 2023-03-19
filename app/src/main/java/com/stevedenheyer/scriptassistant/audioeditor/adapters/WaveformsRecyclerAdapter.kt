package com.stevedenheyer.scriptassistant.audioeditor.adapters

import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerItemView
import com.stevedenheyer.scriptassistant.views.WaveformView

class WaveformsRecyclerAdapter : ListAdapter<WaveformRecyclerItemView, WaveformsRecyclerAdapter.ViewHolder>(WaveformViewDiffCallback){
    private var textChangeListener: OnTextChangeListener? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val waveform = view.findViewById<WaveformView>(R.id.waveform_item)

        init {
            waveform.setOnDragListener { view, event ->
             when (event.action) {
                 DragEvent.ACTION_DRAG_STARTED -> {
                     Log.d("ADP", "Drag started...")
                     true
                 }
                 DragEvent.ACTION_DRAG_ENTERED -> {
                     Log.d("ADP", "Drag entered... $view.id")
                     true
                 }
                 DragEvent.ACTION_DROP -> {
                     Log.d("ADP", "Dropped...")
                     if (textChangeListener != null) {
                         val text = event.clipData.getItemAt(0).text.toString()
                         val waveformItem = getItem(adapterPosition).copy(text = text)

                         textChangeListener!!.onTextChanged(waveformItem)
                     }
                     true
                 }
                 else -> {
                     false
                 }
             }
            }
        }

        fun bind(item: WaveformRecyclerItemView) {
            Log.d("REC", "updating wfm: ${item.waveform.size}")
            waveform.setWaveform(item.waveform)
            waveform.setAudioRange(item.range)
            waveform.setTitle(item.text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.waveform_item, parent, false)

        Log.d("REC", "Recycler created...")

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun setOnTextChangeListener(listener: OnTextChangeListener?) {
        textChangeListener = listener
    }

    object WaveformViewDiffCallback : DiffUtil.ItemCallback<WaveformRecyclerItemView>() {
        override fun areItemsTheSame(oldItem: WaveformRecyclerItemView, newItem: WaveformRecyclerItemView): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WaveformRecyclerItemView, newItem: WaveformRecyclerItemView): Boolean {
            return oldItem.range == newItem.range && oldItem.text == newItem.text && newItem.waveform.size == oldItem.waveform.size
        }
    }

    interface OnTextChangeListener {
        fun onTextChanged(waveformItem: WaveformRecyclerItemView)
    }

}