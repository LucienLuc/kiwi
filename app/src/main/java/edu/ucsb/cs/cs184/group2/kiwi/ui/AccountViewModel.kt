package edu.ucsb.cs.cs184.group2.kiwi.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class AccountViewModel : ViewModel() {
    val _account = MutableLiveData<GoogleSignInAccount>().apply {
        value = null
    }
    var account : LiveData<GoogleSignInAccount> = _account

    fun updateAccount(acc : GoogleSignInAccount?) {
        val _acc = MutableLiveData<GoogleSignInAccount>().apply {
            value = acc
        }
        account = _acc;
    }
}