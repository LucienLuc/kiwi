package edu.ucsb.cs.cs184.group2.kiwi.ui.dashboard

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
        val dashboardViewModel =
            ViewModelProvider(this).get(EventDescriptionViewModel::class.java)

        _binding = FragmentEventDescriptionBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val eventText: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            eventText.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}