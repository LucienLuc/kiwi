package edu.ucsb.cs.cs184.group2.kiwi.ui.eventUpdates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentEventUpdatesBinding
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.hideKeyboard

class eventUpdatesFragment : Fragment() {

    private var _binding: FragmentEventUpdatesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var notification = arrayOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventUpdatesViewModel =
            ViewModelProvider(this).get(EventUpdatesViewModel::class.java)
        _binding = FragmentEventUpdatesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val send: Button = binding.send
        val editText: EditText = binding.editText

        send.setOnClickListener { view->
            view.hideKeyboard()
            handleSend(editText)
            editText.setText("")
        }

        val textView: TextView = binding.username
        textView.text = "Lucien Luc"
        return root
    }

    private fun handleSend(editText: EditText) {
        val text = editText.text
        val name: TextView = binding.username
        name.text = text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}