package edu.ucsb.cs.cs184.group2.kiwi.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    val _text = MutableLiveData<String>().apply {
        value = "This is the login page."
    }
    var text : LiveData<String> = _text

    fun updateText(t : String) {
        val _greet = MutableLiveData<String>().apply {
            value = t
        }
        text = _greet
    }
}