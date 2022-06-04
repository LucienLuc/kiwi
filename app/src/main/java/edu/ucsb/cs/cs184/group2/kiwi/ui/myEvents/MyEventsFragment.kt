package edu.ucsb.cs.cs184.group2.kiwi.ui.myEvents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.firebase.database.*
import edu.ucsb.cs.cs184.group2.kiwi.R

import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentMyEventsBinding
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.Event
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.convertTime
import edu.ucsb.cs.cs184.group2.kiwi.ui.eventDescription.EventDescriptionViewModel
import edu.ucsb.cs.cs184.group2.kiwi.views.EventsView
import java.util.*
import kotlin.collections.ArrayList

class MyEventsFragment : Fragment() {

    private var _binding: FragmentMyEventsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var dr: DatabaseReference

    private var viewList: ArrayList<View> = ArrayList()
    private var viewListIds: ArrayList<Int> = ArrayList()

    private val followedEventsViewModel: FollowedEventsViewModel by activityViewModels()
    private val createdEventsViewModel:CreatedEventsViewModel by activityViewModels()
    private val eventDescriptionViewModel: EventDescriptionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyEventsBinding.inflate(inflater, container, false)
        val constraintLayout: ConstraintLayout = ConstraintLayout(requireContext())
        val constraintSet: ConstraintSet = ConstraintSet()
        dr = FirebaseDatabase.getInstance().getReference("events")

        viewList.clear()
        viewListIds.clear()

        followedEventsViewModel.events.observe(viewLifecycleOwner) {
            viewList.clear()
            viewListIds.clear()
            for (e in it) {
                val eventView: EventsView = EventsView(requireContext())
                eventView.id = View.generateViewId()
                eventView.setName(e.name)

                eventView.setLocation(e.location)
                val c: Calendar = Calendar.getInstance()
                c.timeInMillis = e.datetime

                val time: String = convertTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
                val date: String = (c.get(Calendar.MONTH)+1).toString() + "/" + c.get(Calendar.DAY_OF_MONTH).toString() + "/" + c.get(Calendar.YEAR).toString()

                eventView.setDate(date)
                eventView.setTime(time)

                eventView.setOnClickListener{ view ->
                    val event: Event = Event(e.firebase_id, e.name, e.datetime, e.location, e.description)
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


        binding.scrollViewMyEvents.addView(constraintLayout)
        return binding.root
    }

    fun addToViewList(s:DataSnapshot){
        val e = s.getValue(Event::class.java)
        if(e != null){
            val eventView: EventsView = EventsView(requireContext())
            eventView.id = View.generateViewId()
            eventView.setName(e.name)

            eventView.setLocation(e.location)
            val c: Calendar = Calendar.getInstance()
            c.timeInMillis = e.datetime

            val time: String = convertTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
            val date: String = (c.get(Calendar.MONTH)+1).toString() + "/" + c.get(Calendar.DAY_OF_MONTH).toString() + "/" + c.get(
                Calendar.YEAR).toString()

            eventView.setDate(date)
            eventView.setTime(time)

            eventView.setOnClickListener{ view ->
                val event: Event = Event(e.firebase_id, e.name, e.datetime, e.location, e.description)
                eventDescriptionViewModel.setEvent(event)

                view.findNavController().navigate(R.id.action_navigation_events_list_to_navigation_events_description)
            }

            viewList.add(eventView)
            viewListIds.add(eventView.id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}