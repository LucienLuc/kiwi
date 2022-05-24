package edu.ucsb.cs.cs184.group2.kiwi.ui.eventsList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView.OnEditorActionListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentEventsListBinding
import edu.ucsb.cs.cs184.group2.kiwi.views.EventsView


class EventsListFragment : Fragment() {

    private var _binding: FragmentEventsListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(EventsListViewModel::class.java)

        _binding = FragmentEventsListBinding.inflate(inflater, container, false)

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        val search: SearchView = binding.searchEditText
        // search.setOnQueryTextFocusChangeListener{performSearch()}



        val constraintLayout: ConstraintLayout = ConstraintLayout(requireContext())

        val viewList: ArrayList<View> = ArrayList()
        val viewListIds: ArrayList<Int> = ArrayList()
        for (i in 1..12) {
            val event: EventsView = EventsView(requireContext())
            event.id = View.generateViewId()
            event.setName("Test Name $i")
            event.setLocation("Test Location $i")
            event.setDate("Test Date $i")
            event.setTime("Test time $i")
            viewList.add(event)
            viewListIds.add(event.id)
            constraintLayout.addView(event)
        }

        val constraintSet: ConstraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        var previousItem: View? = null
        for (view in viewList) {
            val lastItem = viewList.indexOf(view) === viewList.size - 1
            if (previousItem == null) {
                constraintSet.connect(
                    view.id,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT
                )
            } else {
                constraintSet.connect(
                    view.id,
                    ConstraintSet.BOTTOM,
                    previousItem.getId(),
                    ConstraintSet.TOP
                )
                if (lastItem) {
                    constraintSet.connect(
                        view.id,
                        ConstraintSet.RIGHT,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.RIGHT
                    )
                }
            }
            previousItem = view
        }

        constraintSet.createVerticalChain(
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            viewListIds.toIntArray(),
            null,
            ConstraintSet.CHAIN_PACKED
        )
        constraintSet.applyTo(constraintLayout)

        binding.scrollView.addView(constraintLayout)

        val root: View = binding.root
        return root
    }

    private fun performSearch(){
        //
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}