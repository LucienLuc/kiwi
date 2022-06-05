package edu.ucsb.cs.cs184.group2.kiwi.ui.myEvents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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

    private var viewListFollow: ArrayList<View> = ArrayList()
    private var viewListIdsFollow: ArrayList<Int> = ArrayList()
    private var viewListCreate: ArrayList<View> = ArrayList()
    private var viewListIdsCreate: ArrayList<Int> = ArrayList()

    private val followedEventsViewModel: FollowedEventsViewModel by activityViewModels()
    private val createdEventsViewModel:CreatedEventsViewModel by activityViewModels()
    private val eventDescriptionViewModel: EventDescriptionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyEventsBinding.inflate(inflater, container, false)
        val constraintLayoutFollow: ConstraintLayout = ConstraintLayout(requireContext())
        val constraintSetFollow: ConstraintSet = ConstraintSet()
        val constraintLayoutCreate: ConstraintLayout = ConstraintLayout(requireContext())
        val constraintSetCreate: ConstraintSet = ConstraintSet()
        dr = FirebaseDatabase.getInstance().getReference("events")

        followedEventsViewModel.events.observe(viewLifecycleOwner) {
            viewListFollow.clear()
            viewListIdsFollow.clear()
            if (it.size == 0) {
                constraintLayoutFollow.removeAllViews()
                val emptyFollowEventsText: TextView = TextView(requireContext())
                emptyFollowEventsText.id = View.generateViewId()
                emptyFollowEventsText.text = resources.getString(R.string.empty_my_upcoming_events)

                constraintLayoutFollow.addView(emptyFollowEventsText)

                constraintSetFollow.clone(constraintLayoutFollow)
            }
            else {
                for (e in it) {
                    val eventView: EventsView = EventsView(requireContext())
                    eventView.id = View.generateViewId()
                    eventView.setName(e.name)

                    eventView.setLocation(e.location)
                    val c: Calendar = Calendar.getInstance()
                    c.timeInMillis = e.datetime

                    val time: String =
                        convertTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
                    val date: String =
                        (c.get(Calendar.MONTH) + 1).toString() + "/" + c.get(Calendar.DAY_OF_MONTH)
                            .toString() + "/" + c.get(Calendar.YEAR).toString()

                    eventView.setDate(date)
                    eventView.setTime(time)

                    eventView.setOnClickListener { view ->
                        val event: Event = Event(
                            e.firebase_id,
                            e.name,
                            e.hosted_by,
                            e.datetime,
                            e.location,
                            e.description,
                            e.updates
                        )
                        eventDescriptionViewModel.setEvent(event)

                        view.findNavController()
                            .navigate(R.id.action_navigation_my_events_to_navigation_events_description)
                    }

                    viewListFollow.add(eventView)
                    viewListIdsFollow.add(eventView.id)

                }

                constraintLayoutFollow.removeAllViews()
                for (event in viewListFollow) {
                    constraintLayoutFollow.addView(event)
                }

                constraintSetFollow.clone(constraintLayoutFollow)
                var previousItem: View? = null
                for (view in viewListFollow) {
                    val lastItem = viewListFollow.indexOf(view) === viewListFollow.size - 1
                    if (previousItem == null) {
                        constraintSetFollow.connect(
                            view.id,
                            ConstraintSet.LEFT,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.LEFT
                        )
                    } else {
                        constraintSetFollow.connect(
                            view.id,
                            ConstraintSet.BOTTOM,
                            previousItem.getId(),
                            ConstraintSet.TOP
                        )
                        if (lastItem) {
                            constraintSetFollow.connect(
                                view.id,
                                ConstraintSet.RIGHT,
                                ConstraintSet.PARENT_ID,
                                ConstraintSet.RIGHT
                            )
                        }
                    }
                    previousItem = view
                }

                if (viewListFollow.size > 1) {
                    constraintSetFollow.createVerticalChain(
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.BOTTOM,
                        viewListIdsFollow.toIntArray(),
                        null,
                        ConstraintSet.CHAIN_PACKED
                    )
                }
                constraintSetFollow.applyTo(constraintLayoutFollow)
            }

        }

        createdEventsViewModel.events.observe(viewLifecycleOwner) {
            viewListCreate.clear()
            viewListIdsCreate.clear()
            if (it.size == 0) {
                constraintLayoutCreate.removeAllViews()
                val emptyCreateEventsText: TextView = TextView(requireContext())
                emptyCreateEventsText.id = View.generateViewId()
                emptyCreateEventsText.text = resources.getString(R.string.empty_my_hosted_event)

                constraintLayoutCreate.addView(emptyCreateEventsText)

                constraintSetCreate.clone(constraintLayoutCreate)
            } else {
                for (e in it) {
                    val eventView: EventsView = EventsView(requireContext())
                    eventView.id = View.generateViewId()
                    eventView.setName(e.name)

                    eventView.setLocation(e.location)
                    val c: Calendar = Calendar.getInstance()
                    c.timeInMillis = e.datetime

                    val time: String =
                        convertTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
                    val date: String =
                        (c.get(Calendar.MONTH) + 1).toString() + "/" + c.get(Calendar.DAY_OF_MONTH)
                            .toString() + "/" + c.get(Calendar.YEAR).toString()

                    eventView.setDate(date)
                    eventView.setTime(time)

                    eventView.setOnClickListener { view ->
                        val event: Event = Event(
                            e.firebase_id,
                            e.name,
                            e.hosted_by,
                            e.datetime,
                            e.location,
                            e.description,
                            e.updates
                        )
                        eventDescriptionViewModel.setEvent(event)

                        view.findNavController()
                            .navigate(R.id.action_navigation_my_events_to_navigation_events_description)
                    }

                    viewListCreate.add(eventView)
                    viewListIdsCreate.add(eventView.id)

                }

                constraintLayoutCreate.removeAllViews()
                for (event in viewListCreate) {
                    constraintLayoutCreate.addView(event)
                }

                constraintSetCreate.clone(constraintLayoutCreate)
                var previousItemCreate: View? = null
                for (view in viewListCreate) {
                    val lastItem = viewListCreate.indexOf(view) === viewListCreate.size - 1
                    if (previousItemCreate == null) {
                        constraintSetCreate.connect(
                            view.id,
                            ConstraintSet.LEFT,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.LEFT
                        )
                    } else {
                        constraintSetCreate.connect(
                            view.id,
                            ConstraintSet.BOTTOM,
                            previousItemCreate.getId(),
                            ConstraintSet.TOP
                        )
                        if (lastItem) {
                            constraintSetCreate.connect(
                                view.id,
                                ConstraintSet.RIGHT,
                                ConstraintSet.PARENT_ID,
                                ConstraintSet.RIGHT
                            )
                        }
                    }
                    previousItemCreate = view
                }

                if (viewListCreate.size > 1) {
                    constraintSetCreate.createVerticalChain(
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.BOTTOM,
                        viewListIdsCreate.toIntArray(),
                        null,
                        ConstraintSet.CHAIN_PACKED
                    )
                }
                constraintSetCreate.applyTo(constraintLayoutCreate)
            }
        }

        binding.scrollViewMyEvents.addView(constraintLayoutFollow)
        binding.scrollViewHostedEvents.addView(constraintLayoutCreate)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}