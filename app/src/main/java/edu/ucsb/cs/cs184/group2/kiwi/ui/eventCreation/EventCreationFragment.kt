package edu.ucsb.cs.cs184.group2.kiwi.ui.eventCreation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.ucsb.cs.cs184.group2.kiwi.R
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentEventCreationBinding
import edu.ucsb.cs.cs184.group2.kiwi.ui.AccountViewModel
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.hideKeyboard
import java.util.*
import kotlin.collections.HashMap


class EventCreationFragment : Fragment() {

    private var _binding: FragmentEventCreationBinding? = null

    //  This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val accountViewModel: AccountViewModel by activityViewModels()
    private var account : GoogleSignInAccount? = null

    // For Time Text View
    private val cal: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventCreationViewModel = ViewModelProvider(this)[EventCreationViewModel::class.java]
        _binding = FragmentEventCreationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val submitButton: Button = binding.buttonSubmit

        submitButton.setOnClickListener{ view->
            view.hideKeyboard()
            handleSubmit()
        }

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

        val timeTextView: TextView = binding.editTextTime
        timeTextView.inputType = InputType.TYPE_NULL

        timeTextView.setOnClickListener(View.OnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                timeTextView.text = convertTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            }
            TimePickerDialog(requireContext(), timeSetListener, cal
                .get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        })

        val dateTextView: TextView = binding.editTextDate
        dateTextView.inputType = InputType.TYPE_NULL
        dateTextView.setOnClickListener(View.OnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                val dateText: String = (month+1).toString() + "/" + day.toString() + "/" + year.toString()
                dateTextView.text = dateText
            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        })

        return root
    }
    //Converts from 24 hour time to 12 hour time
    private fun convertTime(hours: Int, minutes: Int ) :String {

        var res: String = ""
        var isPm: Boolean = false
        if (hours > 12) {
            res += (hours-12).toString() + ":"
            isPm = true
        } else {
            res += hours.toString() + ":"
        }

        if (minutes < 10) {
            res += "0"
        }

        res += minutes

        if (isPm) {
            res += " PM"
        }
        else {
            res += " AM"
        }

        return res
    }

    private fun validateFields(): Boolean {
        val nameTextView: TextView = binding.editTextName
        val timeTextView: TextView = binding.editTextTime
        val dateTextView: TextView = binding.editTextDate
        val locationTextView: TextView = binding.editTextLocation

        if (nameTextView.length() == 0 || timeTextView.length() == 0 || dateTextView.length() == 0 || locationTextView.length() == 0) {
            return false
        }
        else {
            return true
        }
    }

    private fun handleSubmit() {

        val nameTextView: TextView = binding.editTextName
        val timeTextView: TextView = binding.editTextTime
        val dateTextView: TextView = binding.editTextDate
        val locationTextView: TextView = binding.editTextLocation
        val descriptionTextView: TextView = binding.editTextDescription

        val validSubmission: Boolean = validateFields()

        if (validSubmission) {
            val database = Firebase.database
            val eventsRef: DatabaseReference = database.getReference("events")
            val keyedEventsReference: DatabaseReference = eventsRef.push()

            // Update events
            val values: MutableMap<String, Any> = HashMap()
            val name = nameTextView.text.toString().replaceFirstChar { it.uppercase() }
            values["name"] = name
            values["datetime"] = cal.timeInMillis
            values["location"] = locationTextView.text.toString()
            values["description"] = descriptionTextView.text.toString()

            keyedEventsReference.updateChildren(values)

            // Update user
            accountViewModel.account.observe(viewLifecycleOwner) {
                account = it
            }
            if (account == null) {
                Snackbar.make(requireView(), R.string.login_error, Snackbar.LENGTH_SHORT).show()
            }
            else {
                Log.i("EventCreationFragment", account!!.id.toString())
                val usersRef: DatabaseReference = database.getReference("users/" + account!!.id + "/created_events")
                val keyedUserReference: DatabaseReference = usersRef.push()

                val userValues: MutableMap<String, Any> = HashMap()
                userValues["id"] = keyedEventsReference.key.toString()
                keyedUserReference.setValue(userValues)

                Snackbar.make(requireView(), R.string.event_creation_success, Snackbar.LENGTH_SHORT).show()

                nameTextView.text = ""
                timeTextView.text = ""
                dateTextView.text = ""
                locationTextView.text = ""
                descriptionTextView.text = ""
            }
        }
        else {
            Snackbar.make(requireView(),  R.string.event_creation_fail, Snackbar.LENGTH_SHORT).show()
        }
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