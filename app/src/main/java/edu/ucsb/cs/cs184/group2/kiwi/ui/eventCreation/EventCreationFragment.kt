package edu.ucsb.cs.cs184.group2.kiwi.ui.eventCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentEventCreationBinding

class EventCreationFragment : Fragment() {

    private var _binding: FragmentEventCreationBinding? = null

    //  This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventCreationViewModel = ViewModelProvider(this)[EventCreationViewModel::class.java]
        _binding = FragmentEventCreationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val submitButton: Button = binding.buttonSubmit

        submitButton.setOnClickListener{handleSubmit()}

        eventCreationViewModel.editTextName.observe(viewLifecycleOwner){
            binding.editTextName.text = it
        }
        eventCreationViewModel.editTextTime.observe(viewLifecycleOwner){
            binding.editTextTime.text = it
        }
        eventCreationViewModel.editTextDate.observe(viewLifecycleOwner){
            binding.editTextDate.text = it
        }
        eventCreationViewModel.editTextLocation.observe(viewLifecycleOwner){
            binding.editTextLocation.text = it
        }
        eventCreationViewModel.editTextDescription.observe(viewLifecycleOwner){
            binding.editTextDescription.text = it
        }

        return root
    }

    private fun handleSubmit() {
        val database = Firebase.database
        val eventsRef: DatabaseReference = database.getReference("events")

        val nameTextView: TextView = binding.editTextName
        val timeTextView: TextView = binding.editTextTime
        val dateTextView: TextView = binding.editTextDate
        val locationTextView: TextView = binding.editTextLocation
        val descriptionTextView: TextView = binding.editTextDescription

        val keyedEventsReference: DatabaseReference = eventsRef.push()

        val values: MutableMap<String, Any> = HashMap()
        values["name"] = nameTextView.text.toString()
        values["time"] = timeTextView.text.toString()
        values["date"] = dateTextView.text.toString()
        values["location"] = locationTextView.text.toString()
        values["description"] = descriptionTextView.text.toString()

        keyedEventsReference.setValue(values)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        val eventCreationViewModel = ViewModelProvider(this)[EventCreationViewModel::class.java]
        eventCreationViewModel.updateName(binding.editTextName.text)
        eventCreationViewModel.updateTime(binding.editTextTime.text)
        eventCreationViewModel.updateDate(binding.editTextDate.text)
        eventCreationViewModel.updateLocation(binding.editTextLocation.text)
        eventCreationViewModel.updateDescription(binding.editTextDescription.text)

        _binding = null
    }
}