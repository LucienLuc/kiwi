package edu.ucsb.cs.cs184.group2.kiwi.ui.eventDescription

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventDescriptionViewModel : ViewModel() {
    private val _textName = MutableLiveData<String>()
    private val _textHostName = MutableLiveData<String>()
    private val _textDate = MutableLiveData<String>()
    private val _textTime = MutableLiveData<String>()
    private val _textDescription = MutableLiveData<String>()
    private val _textLocation = MutableLiveData<String>()

    fun updateName(s: String){
        _textName.value = s
    }
    fun updateHostName(s: String){
        _textHostName.value = s
    }
    fun updateDate(s: String){
        _textDate.value = s
    }
    fun updateTime(s: String){
        _textTime.value = s
    }
    fun updateDescription(s: String){
        _textDescription.value = s
    }
    fun updateLocation(s: String){
        _textLocation.value = s
    }

    val textName: LiveData<String> = _textName
    val textHostName: LiveData<String> = _textHostName
    val textDate: LiveData<String> = _textDate
    val textTime: LiveData<String> = _textTime
    val textDescription: LiveData<String> = _textDescription
    val textLocation: LiveData<String> = _textLocation
}