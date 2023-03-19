package com.stevedenheyer.scriptassistant.scripteditor

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemDetailsLookup.ItemDetails
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.databinding.ScriptEditorItemBinding
import org.w3c.dom.Text

class ScriptEditorRecyclerAdapter : ListAdapter<ScriptLineRecyclerItemView, ScriptEditorRecyclerAdapter.ViewHolder>(LineDiffCallback)  {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    inner class ViewHolder(private val binding: ScriptEditorItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScriptLineRecyclerItemView) {
            Log.d("SCPADP", "Binding: ${item.index} ${item.text}")
            binding.line = item
        }

        fun getItemDetails(): ItemDetails<Long> =
            object: ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition

                override fun getSelectionKey(): Long = itemId

                override fun inSelectionHotspot(e: MotionEvent): Boolean {
                    return true
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ScriptEditorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RCYADP", "Binding...")
        getItem(position).let { item ->
            holder.bind(item)
            if (!tracker!!.hasSelection() && position == (itemCount - 1)) {
                tracker?.select(item.id)
            }
        }
    }

    override fun getItemId(position: Int) = getItem(position).id

    object LineDiffCallback : DiffUtil.ItemCallback<ScriptLineRecyclerItemView>() {
        override fun areItemsTheSame(oldItem: ScriptLineRecyclerItemView, newItem: ScriptLineRecyclerItemView): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ScriptLineRecyclerItemView, newItem: ScriptLineRecyclerItemView): Boolean {
            return oldItem.equals(newItem)
        }
    }
}