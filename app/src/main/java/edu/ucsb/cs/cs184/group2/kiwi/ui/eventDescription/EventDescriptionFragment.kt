package edu.ucsb.cs.cs184.group2.kiwi.ui.eventDescription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentEventDescriptionBinding

class EventDescriptionFragment : Fragment() {

    private var _binding: FragmentEventDescriptionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventDescriptionViewModel = ViewModelProvider(this).get(EventDescriptionViewModel::class.java)

        _binding = FragmentEventDescriptionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val eventText: TextView = binding.eventText
        val hostText: TextView = binding.hostText
        val dateText: TextView = binding.dateText
        val descriptionText: TextView = binding.descriptionText
        val locationText: TextView = binding.locationText

        eventDescriptionViewModel.textName.observe(viewLifecycleOwner){
            binding.textViewEventName.text = it
        }
        eventDescriptionViewModel.textHostName.observe(viewLifecycleOwner){
            binding.textViewHostName.text = it
        }
        eventDescriptionViewModel.textTime.observe(viewLifecycleOwner){
            binding.textViewEventTime.text = it
        }
        eventDescriptionViewModel.textDate.observe(viewLifecycleOwner){
            binding.textViewEventDate.text = it
        }
        eventDescriptionViewModel.textLocation.observe(viewLifecycleOwner){
            binding.textViewEventLocation.text = it
        }
        eventDescriptionViewModel.textDescription.observe(viewLifecycleOwner){
            binding.textViewEventDescription.text = it
        }

        eventDescriptionViewModel.updateName("Anime Day")
        eventDescriptionViewModel.updateHostName("Jimmy Brown")
        eventDescriptionViewModel.updateDate("Feb 16th, 2022")
        eventDescriptionViewModel.updateTime("2:00 PM")
        eventDescriptionViewModel.updateDescription("Looking for fellow anime watchers to talk about seasonal anime episodes every week!")
        eventDescriptionViewModel.updateLocation("Tropicana Villas \nIsla Vista CA 93117")
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}