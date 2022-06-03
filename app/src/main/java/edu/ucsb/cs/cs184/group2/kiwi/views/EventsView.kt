package edu.ucsb.cs.cs184.group2.kiwi.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import edu.ucsb.cs.cs184.group2.kiwi.R


class EventsView: ConstraintLayout {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private var nameTextView: TextView? = null
    private var timeTextView: TextView? = null
    private var dateTextView: TextView? = null
    private var locationTextView: TextView? = null
    private var descriptionTextView: TextView? = null

    private fun init() {
        inflate(getContext(), R.layout.compound_event,this);
        nameTextView = findViewById(R.id.name)
        timeTextView = findViewById(R.id.time)
        dateTextView = findViewById(R.id.date)
        locationTextView = findViewById(R.id.location)
        descriptionTextView = findViewById(R.id.descriptionText)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("EventsView", this.nameTextView!!.text.toString())
        return super.onTouchEvent(event)
    }

    fun setName(name: String) {
        nameTextView!!.text = name
    }

    fun setTime(time: String) {
        timeTextView!!.text = time
    }

    fun setDate(date: String) {
        dateTextView!!.text = date
    }

    fun setLocation(location: String) {
        locationTextView!!.text = location
    }

    fun getName():String{
        return nameTextView!!.text.toString()
    }

    fun getTime():String{
        return timeTextView!!.text.toString()
    }

    fun getDate():String{
        return dateTextView!!.text.toString()
    }

    fun getLocation():String{
        return locationTextView!!.text.toString()
    }

    fun getDescription():String{
        return descriptionTextView!!.text.toString()
    }
}