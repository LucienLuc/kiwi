package edu.ucsb.cs.cs184.group2.kiwi.ui.messenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MessengerViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is messenger Fragment"
    }
    val text: LiveData<String> = _text
}