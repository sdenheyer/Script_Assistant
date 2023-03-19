package com.stevedenheyer.scriptassistant.scripteditor

import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.SelectionTracker.SelectionObserver
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView

@BindingMethods(value = [BindingMethod(type = RecyclerView::class, attribute = "android:onItemStateChanged", method = "setOnSelectionChangeListener"), ])

object ScriptEditorBindingAdapter {

    @BindingAdapter("android:updateScript")
    @JvmStatic
    fun bindUpdateScript(view: RecyclerView, data: List<ScriptLineRecyclerItemView>?) {
        Log.d("BNDADP", "submitting script data.... $data.size")
        val adapter = getAdapter(view)
        adapter.submitList(data)
    }

    private fun getAdapter(view: RecyclerView): ScriptEditorRecyclerAdapter {
        return if (view.adapter != null && view.adapter is ScriptEditorRecyclerAdapter) {
            view.adapter as ScriptEditorRecyclerAdapter
        } else {
            val adapter = ScriptEditorRecyclerAdapter()
            view.adapter = adapter

            val tracker = SelectionTracker.Builder(
                "selection",
                view,
                StableIdKeyProvider(view),
                ScriptItemDetailsLookup(view),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectSingleAnything()
            ).build()

            adapter.tracker = tracker

            adapter
        }
    }

    class ScriptItemDetailsLookup(private val recyclerView: RecyclerView) :
            ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view) as ScriptEditorRecyclerAdapter.ViewHolder).getItemDetails()
            }
            return null
        }
    }

    @BindingAdapter(value = ["android:onItemStateChanged", "android:onSelectionChanged", "android:onSelectionRefresh", "onSelectionRestored"], requireAll = false)
    @JvmStatic
    fun setOnSelectionChangeListener(view: RecyclerView,  onItemChanged: OnItemStateChanged?, onSelectionChanged: OnSelectionChanged?, onSelectionRefresh: OnSelectionRefresh?,
                                        onSelectionRestore: OnSelectionRestored?) {
        val adapter = getAdapter(view)
        val callback = object : SelectionObserver<Long>() {
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                super.onItemStateChanged(key, selected)
                Log.d("SAD", "ItemStateChanged: $key, $selected")
                onItemChanged?.onItemStateChanged(key, selected)
            }
        }

        if ((onItemChanged != null || onSelectionChanged != null || onSelectionRefresh != null || onSelectionRestore != null) && adapter.tracker != null) {
            adapter.tracker!!.addObserver(callback)
        }
    }


    interface OnItemStateChanged {
        fun onItemStateChanged(key: Long, selected: Boolean)
    }

    interface OnSelectionChanged {
        fun onSelectionChanged()
    }

    interface OnSelectionRefresh {
        fun onSelectionRefresh()
    }

    interface OnSelectionRestored {
        fun onSelectionRestored()
    }
}