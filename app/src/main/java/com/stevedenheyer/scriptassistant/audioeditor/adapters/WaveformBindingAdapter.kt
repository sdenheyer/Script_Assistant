package com.stevedenheyer.scriptassistant.audioeditor.adapters

import android.util.Log
import androidx.databinding.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.SentencesCollection
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerItemView

@BindingMethods(value = [BindingMethod(type = ViewPager2::class, attribute = "android:currentPage", method = "setCurrentItem"),
                        BindingMethod(type = ViewPager2::class, attribute = "android:currentId", method = "setCurrentId"),
                        BindingMethod(type = RecyclerView::class, attribute = "android:onScriptedDropped", method = "set")])

@InverseBindingMethods(value = [InverseBindingMethod(type = ViewPager2::class, attribute = "android:currentPage"),
                                InverseBindingMethod(type = ViewPager2::class, attribute = "android:currentId")])

object WaveformBindingAdapter {
    //private lateinit var tracker: SelectionTracker<Long>

    @BindingAdapter("android:updateWaveform")
    @JvmStatic
    fun bindUpdateWaveform(view: ViewPager2, data: Array<Waveform>?) {
        val adapter = getAdapter(view)
        adapter.updateWaveform(data)
    }

    @BindingAdapter("android:updateSentences")
    @JvmStatic
    fun bindUpdateSentences(view: ViewPager2, data: Map<Long, SentencesCollection>?) {
        //Log.d("BND", "Sentence update")
        val adapter = getAdapter(view)
        adapter.updateSentences(data)
    }

    @BindingAdapter("android:updateRecyclerItems")
    @JvmStatic
    fun bindUpdateRecycleritems(view: RecyclerView, data: List<WaveformRecyclerItemView>?) {
     //   Log.d("BND", "Recycler update: ${data?.size}")
            val adapter = getAdapter(view)
            adapter.submitList(data)
    }

    private fun getAdapter(view: RecyclerView): WaveformsRecyclerAdapter {
     //   Log.d("ADP", "Getting recycler adapter...")
        return if (view.adapter != null && view.adapter is WaveformsRecyclerAdapter) {
            view.adapter as WaveformsRecyclerAdapter
        } else {

            val adapter = WaveformsRecyclerAdapter()
            //adapter.setHasStableIds(true)
            view.adapter = adapter

     /*       tracker = SelectionTracker.Builder(
                "waveformSeletion",
                view,
                SentencesKeyProvider(adapter),
                SentenceDetailsLookup(view),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectSingleAnything()
            ).withOnDragInitiatedListener { event ->
                Log.d("TCK", "Event Rec'd")
                if (event.action == DragEvent.ACTION_DRAG_ENTERED) {
                    Log.d("ADPT", "Drag entered")
                } else if (event.action == DragEvent.ACTION_DROP) {
                    Log.d("ADPT", "Dropped")
                }
                false
            }.build()

            adapter.tracker = tracker*/

            adapter
        }
    }

    private fun getAdapter(view: ViewPager2): WaveformsUniverseAdapter {
    //    Log.d("ADP", "Getting universe adapter...")
        return if (view.adapter != null && view.adapter is WaveformsUniverseAdapter) {
            view.adapter as WaveformsUniverseAdapter
        } else {
            val adapter = WaveformsUniverseAdapter()
            adapter.setHasStableIds(true)
            view.adapter = adapter
            adapter
        }
    }

    @BindingAdapter("android:currentPage")
    @JvmStatic
    fun setCurrentPage(view: ViewPager2, page: Int) {
        if (page != view.currentItem) {
            view.currentItem = page
            Log.d("TEMP", "setCurrentPage: $page")
        }
    }

    @InverseBindingAdapter(attribute = "android:currentPage", event = "android:currentPageAttrChanged")
    @JvmStatic
    fun getCurrentPage(view: ViewPager2):Int {
      return view.currentItem
    }

    @BindingAdapter("android:currentId")
    @JvmStatic
    fun setCurrentId(view: ViewPager2, position: Int) {
        if (position != view.currentItem) {

        }
    }

    @InverseBindingAdapter(attribute = "android:currentId", event = "android:currentPageAttrChanged")
    @JvmStatic
    fun getCurrentId(view: ViewPager2):Long {
        val adapter = getAdapter(view)
        return adapter.getItemId(view.currentItem)
    }

    @BindingAdapter(value = ["android:onPageSelected", "android:onPageSelectedId", "android:currentPageAttrChanged"], requireAll = false)
    @JvmStatic
    fun setOnPageChangeListener(view: ViewPager2, pageSelected: OnPageChanged?, pageSelectedId: OnPageChangedId?, attrChanged: InverseBindingListener?)
    {
        val adapter = getAdapter(view)
        val callback = object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d("BDAD", "page selected: $position")
                pageSelected?.onPageChanged(position)
                attrChanged?.onChange()

                val id = adapter.getItemId(position)
                pageSelectedId?.onPageChangedId(id)
            }

        }
        if (pageSelected == null && pageSelectedId == null && attrChanged == null) {
            view.unregisterOnPageChangeCallback(callback)
        } else {
            view.registerOnPageChangeCallback(callback)
        }
    }

    @BindingAdapter(value = ["android:onScriptDropped"])
    @JvmStatic
    fun setOnScriptDroppedListener(view: RecyclerView, scriptDropped: OnScriptDropped?) {
        val adapter = getAdapter(view)
        val callback = object: WaveformsRecyclerAdapter.OnTextChangeListener {
            override fun onTextChanged(waveformItem: WaveformRecyclerItemView) {
                scriptDropped?.onScriptDropped(waveformItem)
            }

        }
        if (scriptDropped == null) {
            adapter.setOnTextChangeListener(null)
        } else {
            Log.d("BND", "REC Adapter set...")
            adapter.setOnTextChangeListener(callback)
        }
    }

    interface OnPageChanged {
        fun onPageChanged(page: Int)
    }

    interface OnPageChangedId {
        fun onPageChangedId(id: Long)
    }

    interface OnScriptDropped {
        fun onScriptDropped(waveformRecyclerItemView: WaveformRecyclerItemView)
    }


/*
    class SentencesKeyProvider(private val adapter: WaveformsRecyclerAdapter) : ItemKeyProvider<Long>
        (SCOPE_CACHED) {
        override fun getKey(position: Int): Long = adapter.getItemId(position)

        override fun getPosition(key: Long): Int = adapter.getItemPostition(key)
    }

    class SentenceDetailsLookup(private val recyclerView: RecyclerView): ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            Log.d("SDL", "Event received")
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view) as WaveformsRecyclerAdapter.ViewHolder).getItem()
            }
            return null
        }

    }*/

}

