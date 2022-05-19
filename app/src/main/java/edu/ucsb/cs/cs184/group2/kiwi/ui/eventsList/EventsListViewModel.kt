package edu.ucsb.cs.cs184.group2.kiwi.ui.eventsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventsListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is eventsList Fragment"
    }
    val text: LiveData<String> = _text
}