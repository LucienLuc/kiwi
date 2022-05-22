package edu.ucsb.cs.cs184.group2.kiwi.ui.eventsList

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EventsListViewModel : ViewModel() {

    class Event(val name: String, val date: String, val time: String, val location: String)

    private val _events = MutableLiveData<ArrayList<Event>>()

    fun setEvents(eventsList: ArrayList<Event>) {
        _events.value = eventsList
    }

    val events: LiveData<ArrayList<Event>> = _events

//    private fun loadEvents() {
//        val database = Firebase.database
//        val eventsRef: DatabaseReference = database.getReference("events")
//
//        eventsRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val value = dataSnapshot.value
//                if (dataSnapshot.hasChildren()) {
//                    val eventsList: ArrayList<Event> = ArrayList()
//                    val iter: Iterator<DataSnapshot> = dataSnapshot.children.iterator()
//                    while (iter.hasNext()) {
//                        val snap = iter.next()
//                        val nodId = snap.key
//
//                        val name = snap.child("name").value as String
//                        val date = snap.child("date").value as String
//                        val time = snap.child("time").value as String
//                        val location = snap.child("location").value as String
//
//                        val event: Event = Event(name, date, time, location)
//                        eventsList.add(event)
//                        //received results
//                        Log.i("FirebaseLog", "$name on nod $nodId")
//                    }
//                }
//                Log.d("FirebaseLog", "Value is: $value")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w("FirebaseLog", "Failed to read value.", error.toException())
//            }
//        })
//    }
}