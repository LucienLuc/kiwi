package edu.ucsb.cs.cs184.group2.kiwi.ui.eventCreation

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventCreationViewModel : ViewModel() {
    private val _editTextName = MutableLiveData<Editable>()
    private val _editTextTime = MutableLiveData<Editable>()
    private val _editTextDate = MutableLiveData<Editable>()
    private val _editTextLocation = MutableLiveData<Editable>()
    private val _editTextDescription = MutableLiveData<Editable>()


    val editTextName: LiveData<Editable> = _editTextName
    val editTextTime: LiveData<Editable> = _editTextTime
    val editTextDate: LiveData<Editable> = _editTextDate
    val editTextLocation: LiveData<Editable> = _editTextLocation
    val editTextDescription: LiveData<Editable> = _editTextDescription
}