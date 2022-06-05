package edu.ucsb.cs.cs184.group2.kiwi.ui.eventsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.Event

class EventsListViewModel : ViewModel() {
    private val _events = MutableLiveData<ArrayList<Event>>()
    private val _query = MutableLiveData<String>()


    fun setEvents(eventsList: ArrayList<Event>) {
        _events.value = eventsList
    }
    fun setQuery(q:String){

    }


    val events: LiveData<ArrayList<Event>> = _events

}