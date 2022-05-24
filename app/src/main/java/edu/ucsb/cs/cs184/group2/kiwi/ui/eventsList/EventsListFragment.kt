package edu.ucsb.cs.cs184.group2.kiwi.ui.eventsList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import edu.ucsb.cs.cs184.group2.kiwi.R
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentEventsListBinding
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.Event
import edu.ucsb.cs.cs184.group2.kiwi.ui.eventDescription.EventDescriptionViewModel
import edu.ucsb.cs.cs184.group2.kiwi.views.EventsView

class EventsListFragment : Fragment() {

    private var _binding: FragmentEventsListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var viewList: ArrayList<View> = ArrayList()
    private var viewListIds: ArrayList<Int> = ArrayList()

    private val eventsListViewModel: EventsListViewModel by activityViewModels()
    private val eventDescriptionViewModel: EventDescriptionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEventsListBinding.inflate(inflater, container, false)
        val constraintLayout: ConstraintLayout = ConstraintLayout(requireContext())
        val constraintSet: ConstraintSet = ConstraintSet()

        eventsListViewModel.events.observe(viewLifecycleOwner) { it ->
            viewList.clear()
            viewListIds.clear()
            for (e in it) {
                val eventView: EventsView = EventsView(requireContext())
                eventView.id = View.generateViewId()
                eventView.setName(e.name)
                eventView.setTime(e.time)
                eventView.setDate(e.date)
                eventView.setLocation(e.location)

                eventView.setOnClickListener{ view ->
                    val event: Event = Event(e.name, e.date, e.time, e.location, e.description)
                    eventDescriptionViewModel.setEvent(event)

                    view.findNavController().navigate(R.id.action_navigation_events_list_to_navigation_events_description)
                }

                viewList.add(eventView)
                viewListIds.add(eventView.id)

            }

            constraintLayout.removeAllViews()
            for (event in viewList) {
                constraintLayout.addView(event)
            }

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

            if (viewList.size > 1) {
                constraintSet.createVerticalChain(
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM,
                    viewListIds.toIntArray(),
                    null,
                    ConstraintSet.CHAIN_PACKED
                )
            }
            constraintSet.applyTo(constraintLayout)

        }

        binding.root.addView(constraintLayout)

        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}