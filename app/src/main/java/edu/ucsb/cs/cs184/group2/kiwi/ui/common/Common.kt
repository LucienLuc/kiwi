package edu.ucsb.cs.cs184.group2.kiwi.ui.common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

//Converts from 24 hour time to 12 hour time
fun convertTime(hours: Int, minutes: Int ) :String {

    var res: String = ""
    var isPm: Boolean = false
    if (hours > 12) {
        res += (hours-12).toString() + ":"
        isPm = true
    } else {
        res += hours.toString() + ":"
    }

    if (minutes < 10) {
        res += "0"
    }

    res += minutes

    if (isPm) {
        res += " PM"
    }
    else {
        res += " AM"
    }

    return res
}
