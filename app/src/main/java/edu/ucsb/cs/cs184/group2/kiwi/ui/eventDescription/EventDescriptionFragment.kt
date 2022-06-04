package edu.ucsb.cs.cs184.group2.kiwi.ui.eventDescription

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.ucsb.cs.cs184.group2.kiwi.R
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentEventDescriptionBinding
import edu.ucsb.cs.cs184.group2.kiwi.ui.AccountViewModel
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.convertTime
import java.util.*
import kotlin.collections.HashMap

class EventDescriptionFragment : Fragment() {

    private var _binding: FragmentEventDescriptionBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val eventDescriptionViewModel: EventDescriptionViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()

    private var account: GoogleSignInAccount? = null
    private var event_id: String? = null

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
        val updateTextDescription : TextView? = binding.textViewUpdateDescription

        setHasOptionsMenu(true)

        accountViewModel.account.observe(viewLifecycleOwner) {
            account = it
            updateFollowButton()
        }

        eventDescriptionViewModel.followButtonText.observe(viewLifecycleOwner) {
            binding.buttonFollowEvent.text = it
        }

        eventDescriptionViewModel.event.observe(viewLifecycleOwner) { event->
            eventText.text = event.name
//            Need to add Host to database and viewmodel and class
//            hostText.text = event.host
            val c: Calendar = Calendar.getInstance()
            c.timeInMillis = event.datetime

            val time: String = convertTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
            val date: String = (c.get(Calendar.MONTH)+1).toString() + "/" + c.get(Calendar.DAY_OF_MONTH).toString() + "/" + c.get(Calendar.YEAR).toString()

            timeText.text = time
            dateText.text = date
            locationText.text = event.location
            descriptionText.text = event.description
            updateTextDescription?.text = event.updates
            binding.updateEditTextView.setText(event.updates)
            event_id = event.firebase_id

            handleHostedEvent()

            Log.i("EventDescription", "Time = $time")
            Log.i("EventDescription", "Date = $date")
            Log.i("EventDescription", "Location = ${event.location}")
            Log.i("EventDescription", "Description = ${event.description}")
            Log.i("EventDescription", "Description = ${event.updates}")
            Log.i("EventDescription", "Event ID = ${event.firebase_id}")
        }

        binding.buttonFollowEvent.setOnClickListener{
            handleFollowEvent()
        }

        binding.editButton.setOnClickListener{
            var updateText = binding.updateEditTextView?.text.toString()
            updateEvent(updateText)
//            binding.updateEditTextView?.setText("")
        }

        return root
    }

//    class EventPost {
//        var datetime : Int = 0;
//        var description : String = "";
//        var location : String = "";
//        var name : String = "";
//        var updates : String = "";
//    }

    private fun updateEvent(updateText : String) {
        if (event_id == "" || event_id == null) {
            Log.i("EventDescription", "Event not found.")
            return
        }

        val database = Firebase.database
        val eventsRef : DatabaseReference = database.getReference("events/" + event_id.toString())

        val eventValues: MutableMap<String, Any> = HashMap()
        eventDescriptionViewModel.event.observe(viewLifecycleOwner) { event->
            eventValues["datetime"] = event.datetime
            eventValues["description"] = event.description
            eventValues["location"] = event.location
            eventValues["name"] = event.name
            eventValues["updates"] = updateText

            eventDescriptionViewModel.setUpdateText(updateText)
        }

        val updateTextDescription : TextView? = binding.textViewUpdateDescription
        updateTextDescription?.text = updateText
        eventsRef.updateChildren(eventValues)
    }

    private fun handleHostedEvent() {
        val database = Firebase.database
        val usersRef: DatabaseReference = database.getReference("users")

        if (account != null) {
            val userId = account!!.id

            val path = userId!! + "/created_events"
            val keyedUserFollowedEventsReference: DatabaseReference = usersRef.child(path)

            val hostedEventsQuery = keyedUserFollowedEventsReference.orderByValue()
            hostedEventsQuery.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Event host is account
                    for (event in snapshot.children) {
                        for (id in event.children) {
                            if(id.value.toString() == event_id!!) {
                                // Is host
                                binding.buttonFollowEvent.visibility = TextView.INVISIBLE
                                binding.updateEditTextView.visibility = TextView.VISIBLE
                                binding.editButton.visibility = TextView.VISIBLE
                                return
                            }
                        }
                    }
                    // Is not host
                    binding.buttonFollowEvent.visibility = TextView.VISIBLE
                    binding.updateEditTextView.visibility = TextView.INVISIBLE
                    binding.editButton.visibility = TextView.INVISIBLE

                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("FirebaseLog", "Failed to read value.", error.toException())
                }
            })

        }

    }

    private fun handleFollowEvent() {

        val database = Firebase.database
        val usersRef: DatabaseReference = database.getReference("users")

        if (account != null) {
            val userId = account!!.id

            val path = userId!! + "/followed_events"
            val keyedUserFollowedEventsReference: DatabaseReference = usersRef.child(path)

            val followedEventAlreadyFollowedQuery = keyedUserFollowedEventsReference.orderByValue()
            followedEventAlreadyFollowedQuery.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Event already followed
                    for (event in snapshot.children) {
                        for (id in event.children) {
                            if(id.value.toString() == event_id!!) {
                                keyedUserFollowedEventsReference.child(event.key!!).removeValue()
                                eventDescriptionViewModel.setFollowButtonText(requireContext().getString(R.string.description_follow_event_button))
                                return
                            }
                        }
                    }
                    // Event not followed
                    val keyedEventsReference: DatabaseReference = keyedUserFollowedEventsReference.push()
                    val values: MutableMap<String, Any> = HashMap()
                    values["id"] = event_id!!
                    eventDescriptionViewModel.setFollowButtonText(requireContext().getString(R.string.description_unfollow_event_button))

                    keyedEventsReference.setValue(values)
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("FirebaseLog", "Failed to read value.", error.toException())
                }
            })

        }

    }

    private fun updateFollowButton() {
        val database = Firebase.database
        val usersRef: DatabaseReference = database.getReference("users")

        if (account != null) {
            val userId = account!!.id

            val path = userId!! + "/followed_events"
            val keyedUserFollowedEventsReference: DatabaseReference = usersRef.child(path)

            val followedEventAlreadyFollowedQuery = keyedUserFollowedEventsReference.orderByValue()
            followedEventAlreadyFollowedQuery.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Event already followed
                    for (event in snapshot.children) {
                        for (id in event.children) {
                            if( id.value.toString() == event_id!!) {
                                Log.d("EventDescription", "Already followed")
                                eventDescriptionViewModel.setFollowButtonText(requireContext().getString(R.string.description_unfollow_event_button))
                                return
                            }
                        }
                    }
                    // Event not followed
                    Log.d("EventDescription", "Not followed")
                    eventDescriptionViewModel.setFollowButtonText(requireContext().getString(R.string.description_follow_event_button))
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("FirebaseLog", "Failed to read value.", error.toException())
                }
            })


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        findNavController().navigateUp()
        return super.onOptionsItemSelected(item)
    }
}