package edu.ucsb.cs.cs184.group2.kiwi.ui.myEvents

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.Event

class CreatedEventsViewModel : ViewModel() {

    private val _events = MutableLiveData<ArrayList<Event>>()

    fun setEvents(eventsList: ArrayList<Event>) {
        _events.value = eventsList
    }

    val events: LiveData<ArrayList<Event>> = _events
}