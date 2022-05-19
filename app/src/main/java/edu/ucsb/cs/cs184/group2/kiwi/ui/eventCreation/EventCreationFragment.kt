package edu.ucsb.cs.cs184.group2.kiwi.ui.eventCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

        _binding = FragmentEventCreationBinding.inflate(inflater, container, false)
        val root: View = binding.root

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