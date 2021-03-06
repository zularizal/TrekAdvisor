package com.peterlaurence.trekadvisor.menu.mapcreate

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.peterlaurence.trekadvisor.R
import com.peterlaurence.trekadvisor.core.mapsource.MapSource
import com.peterlaurence.trekadvisor.core.mapsource.MapSourceLoader
import com.peterlaurence.trekadvisor.menu.mapcreate.MapSourceAdapter.MapSourceSelectionListener

class MapCreateFragment : Fragment(), MapSourceSelectionListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var mapSourceSet: Array<MapSource>
    private lateinit var nextButton: Button

    private lateinit var selectedMapSource: MapSource
    private lateinit var fragmentListener: MapCreateFragmentInteractionListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MapCreateFragmentInteractionListener) {
            fragmentListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement MapCreateFragmentInteractionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapSourceSet = MapSourceLoader.supportedMapSource
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_map_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextButton = view.findViewById(R.id.next_button)
        nextButton.setOnClickListener { fragmentListener.onMapSourceSelected(selectedMapSource) }

        val viewManager = LinearLayoutManager(context)
        viewAdapter = MapSourceAdapter(mapSourceSet, this, context.getColor(R.color.colorAccent),
                context.getColor(R.color.colorPrimaryTextWhite), context.getColor(R.color.colorPrimaryTextBlack))

        /* Item decoration : divider */
        val dividerItemDecoration = DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL)
        val divider = this.context.getDrawable(R.drawable.divider)
        if (divider != null) {
            dividerItemDecoration.setDrawable(divider)
        }

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_map_create).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onMapSourceSelected(m: MapSource) {
        selectedMapSource = m
        nextButton.visibility = View.VISIBLE
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface MapCreateFragmentInteractionListener {
        fun onMapSourceSelected(mapSource: MapSource)
    }
}