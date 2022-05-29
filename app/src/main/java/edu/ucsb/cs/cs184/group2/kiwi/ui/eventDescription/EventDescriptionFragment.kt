package edu.ucsb.cs.cs184.group2.kiwi.ui.eventDescription

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.ucsb.cs.cs184.group2.kiwi.R
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentEventDescriptionBinding
import edu.ucsb.cs.cs184.group2.kiwi.ui.AccountViewModel

class EventDescriptionFragment : Fragment() {

    private var _binding: FragmentEventDescriptionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val eventDescriptionViewModel: EventDescriptionViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()

    private var account: GoogleSignInAccount? = null
    private var event_id: String? = null

    private var viewStub: ViewStub? = null

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

        accountViewModel.account.observe(viewLifecycleOwner) {
            account = it
            updateFollowButton()
        }

        eventDescriptionViewModel.followButtonText.observe(viewLifecycleOwner) {
            binding.buttonFollowEvent.text = it
        }

        eventDescriptionViewModel.event.observe(viewLifecycleOwner) { event->
            eventText.text = event.name
            // Need to add Host to database and viewmodel and class
//            hostText.text = event.host
            timeText.text = event.time
            dateText.text = event.date
            locationText.text = event.location
            descriptionText.text = event.description

            event_id = event.firebase_id
        }

        binding.buttonFollowEvent.setOnClickListener{
            handleFollowEvent()
        }

//        viewStub = ViewStub(requireContext())
        viewStub.setLayoutResource(R.layout.fragment_event_description);
        return viewStub
//        return root
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
                                return
                            }
                        }
                    }
                    // Event not followed
                    val keyedEventsReference: DatabaseReference = keyedUserFollowedEventsReference.push()
                    val values: MutableMap<String, Any> = HashMap()
                    values["id"] = event_id!!

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
                                viewStub.inflate()
                                return
                            }
                        }
                    }
                    // Event not followed
                    Log.d("EventDescription", "Not followed")
                    eventDescriptionViewModel.setFollowButtonText(requireContext().getString(R.string.description_follow_event_button))
                    viewStub.inflate()
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