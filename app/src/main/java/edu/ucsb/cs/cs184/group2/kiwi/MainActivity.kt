package edu.ucsb.cs.cs184.group2.kiwi

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.ucsb.cs.cs184.group2.kiwi.databinding.ActivityMainBinding
import edu.ucsb.cs.cs184.group2.kiwi.ui.AccountViewModel
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.Event
import edu.ucsb.cs.cs184.group2.kiwi.ui.eventsList.EventsListViewModel
import edu.ucsb.cs.cs184.group2.kiwi.ui.myEvents.CreatedEventsViewModel
import edu.ucsb.cs.cs184.group2.kiwi.ui.myEvents.FollowedEventsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val eventsListViewModel: EventsListViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()
    private val followedEventsViewModel: FollowedEventsViewModel by viewModels()
    private val createdEventsViewModel: CreatedEventsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountViewModel.account.observe(this, Observer { item ->
            startFirebaseListenerOnFollowedEvents(item)
            startFirebaseListenerOnCreatedEvents(item)
        })

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == R.id.navigation_login) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_events_list, R.id.navigation_event_creation,R.id.navigation_login,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        startFirebaseListenerOnAllEvents()

    }

    private fun startFirebaseListenerOnAllEvents() {
        val database = Firebase.database
        val eventsRef: DatabaseReference = database.getReference("events")

        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.value
                if (dataSnapshot.hasChildren()) {
                    val iter: Iterator<DataSnapshot> = dataSnapshot.children.iterator()
                    val eventsList = ArrayList<Event>()
                    while (iter.hasNext()) {
                        val snap = iter.next()
                        val nodId = snap.key

                        val name = snap.child("name").value as String
                        val datetime = snap.child("datetime").value as Long
                        val location = snap.child("location").value as String
                        val description = snap.child("description").value as String
                        val updates = snap.child("updates").value as String

                        if (updates == null) {
                            val event = Event(nodId!!, name, datetime, location, description, "")
                            eventsList.add(event)
                        } else {
                            val event = Event(nodId!!, name, datetime, location, description, updates)
                            eventsList.add(event)
                        }



                        //received results
                        Log.i("FirebaseLog", "$name on nod $nodId")
                    }
                    eventsListViewModel.setEvents(eventsList)

                }
                Log.d("FirebaseLog", "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("FirebaseLog", "Failed to read value.", error.toException())
            }
        })
    }

    private fun startFirebaseListenerOnFollowedEvents(account: GoogleSignInAccount) {
        val database = Firebase.database
        val eventsRef: DatabaseReference = database.getReference("events")
        val usersRef: DatabaseReference = database.getReference("users")
        val userId = account.id
        val path = userId!! + "/followed_events"
        val keyedUserFollowedEventsReference: DatabaseReference = usersRef.child(path)
        val followedEventQuery = keyedUserFollowedEventsReference.orderByValue()

        followedEventQuery.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val followedEventsList = ArrayList<Event>()
                for (event in snapshot.children) {
                    for (id in event.children) {
                        val event_id = id.value.toString()
                        val eventsQuery = eventsRef.orderByKey().startAt(event_id).limitToFirst(1)
                        // Query for more information about that event
                        eventsQuery.addListenerForSingleValueEvent(object:ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    val iter: Iterator<DataSnapshot> = dataSnapshot.children.iterator()
                                    while (iter.hasNext()) {
                                        val snap = iter.next()
                                        val nodId = snap.key

                                        val name = snap.child("name").value as String
                                        val datetime = snap.child("datetime").value as Long
                                        val location = snap.child("location").value as String
                                        val description = snap.child("description").value as String

                                        val e = Event(nodId!!, name, datetime, location, description)
                                        followedEventsList.add(e)

                                        //received results
                                        Log.d("FirebaseLog", "Followed event $name on nod $nodId")
                                    }
                                    followedEventsViewModel.setEvents(followedEventsList)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.w("FirebaseLog", "Failed to read value.", error.toException())
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("FirebaseLog", "Failed to read value.", error.toException())
            }
        })

    }

    private fun startFirebaseListenerOnCreatedEvents(account: GoogleSignInAccount) {
        val database = Firebase.database
        val eventsRef: DatabaseReference = database.getReference("events")
        val usersRef: DatabaseReference = database.getReference("users")
        val userId = account.id
        val path = userId!! + "/created_events"
        val keyedUserFollowedEventsReference: DatabaseReference = usersRef.child(path)
        val followedEventQuery = keyedUserFollowedEventsReference.orderByValue()

        followedEventQuery.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val createdEventsList = ArrayList<Event>()
                for (event in snapshot.children) {
                    for (id in event.children) {
                        val event_id = id.value.toString()
                        val eventsQuery = eventsRef.orderByKey().startAt(event_id).limitToFirst(1)
                        // Query for more information about that event
                        eventsQuery.addListenerForSingleValueEvent(object:ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    val iter: Iterator<DataSnapshot> = dataSnapshot.children.iterator()
                                    while (iter.hasNext()) {
                                        val snap = iter.next()
                                        val nodId = snap.key

                                        val name = snap.child("name").value as String
                                        val datetime = snap.child("datetime").value as Long
                                        val location = snap.child("location").value as String
                                        val description = snap.child("description").value as String

                                        val e = Event(nodId!!, name, datetime, location, description)
                                        createdEventsList.add(e)

                                        //received results
                                        Log.d("FirebaseLog", "Created event $name on nod $nodId")
                                    }
                                    createdEventsViewModel.setEvents(createdEventsList)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.w("FirebaseLog", "Failed to read value.", error.toException())
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("FirebaseLog", "Failed to read value.", error.toException())
            }
        })

    }
}