package edu.ucsb.cs.cs184.group2.kiwi

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.ucsb.cs.cs184.group2.kiwi.databinding.ActivityMainBinding
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.Event
import edu.ucsb.cs.cs184.group2.kiwi.ui.eventsList.EventsListViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val eventsListViewModel: EventsListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_events_list, R.id.navigation_event_creation
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        startFirebaseListener()

    }

    private fun startFirebaseListener() {
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
                        val date = snap.child("date").value as String
                        val time = snap.child("time").value as String
                        val location = snap.child("location").value as String
                        val description = snap.child("description").value as String

                        val event = Event(name, date, time, location, description)
                        eventsList.add(event)

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
}