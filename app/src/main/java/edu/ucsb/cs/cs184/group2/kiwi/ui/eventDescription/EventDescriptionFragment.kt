package edu.ucsb.cs.cs184.group2.kiwi.ui.eventDescription

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentEventDescriptionBinding
import org.w3c.dom.Text


class EventDescriptionFragment : Fragment() {

    private var _binding: FragmentEventDescriptionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val eventDescriptionViewModel: EventDescriptionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEventDescriptionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val eventText: TextView = binding.textViewEventName
        val hostText: TextView = binding.textViewHostName
        val dateText: TextView = binding.textViewEventDate
        val timeText: TextView = binding.textViewEventTime
        val descriptionText: TextView = binding.textViewEventDescription
        val locationText: TextView = binding.textViewEventLocation

        setHasOptionsMenu(true)

        eventDescriptionViewModel.event.observe(viewLifecycleOwner) { event->
            eventText.text = event.name
            // Need to add Host to database and viewmodel and class
//            hostText.text = event.host
            timeText.text = event.time
            dateText.text = event.date
            locationText.text = event.location
            descriptionText.text = event.description
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("EventDescription", "Clicked Back Button")
        findNavController().navigateUp()
        return super.onOptionsItemSelected(item)
    }
}