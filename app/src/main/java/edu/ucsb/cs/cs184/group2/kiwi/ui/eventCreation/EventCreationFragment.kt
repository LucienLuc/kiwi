package edu.ucsb.cs.cs184.group2.kiwi.ui.eventCreation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentEventCreationBinding

class EventCreationFragment : Fragment() {

    private var _binding: FragmentEventCreationBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventCreationViewModel = ViewModelProvider(this).get(EventCreationViewModel::class.java)

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

        val submitButton: Button = binding.buttonSubmit

        submitButton.setOnClickListener{handleSubmit()}

        return root
    }


    private fun handleSubmit() {
        val database = Firebase.database
        val eventsRef: DatabaseReference = database.getReference("events")

        val nameTextView: TextView = binding.editTextName
        val timeTextView: TextView = binding.editTextTime
        val dateTextView: TextView = binding.editTextDate
        val locationTextView: TextView = binding.editTextLocation

        val keyedEventsReference: DatabaseReference = eventsRef.push()

        keyedEventsReference.child("name").setValue(nameTextView.text.toString())
        keyedEventsReference.child("time").setValue(timeTextView.text.toString())
        keyedEventsReference.child("date").setValue(dateTextView.text.toString())
        keyedEventsReference.child("location").setValue(locationTextView.text.toString())

//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val value = dataSnapshot.getValue()
//                Log.d("Test", "Value is: $value")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w("Test", "Failed to read value.", error.toException())
//            }
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val eventCreationViewModel = ViewModelProvider(this).get(EventCreationViewModel::class.java)
        eventCreationViewModel.updateName(binding.editTextName.text)
        eventCreationViewModel.updateTime(binding.editTextName.text)
        eventCreationViewModel.updateDate(binding.editTextName.text)
        eventCreationViewModel.updateLocation(binding.editTextName.text)
        eventCreationViewModel.updateDescription(binding.editTextName.text)

        _binding = null
    }
}