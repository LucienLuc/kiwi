package edu.ucsb.cs.cs184.group2.kiwi.ui.eventDescription

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.ucsb.cs.cs184.group2.kiwi.ui.common.Event

class EventDescriptionViewModel : ViewModel() {

    private val _event = MutableLiveData<Event>()

    private val _followButtonText = MutableLiveData<String>()

    fun setEvent(event: Event) {
        _event.value = event
    }

    fun setFollowButtonText(text: String) {
        _followButtonText.value = text
    }

    val event: LiveData<Event> = _event
    val followButtonText: LiveData<String> = _followButtonText
}