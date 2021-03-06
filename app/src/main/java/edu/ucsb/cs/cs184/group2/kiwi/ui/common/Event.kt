package edu.ucsb.cs.cs184.group2.kiwi.ui.common

class Event(
    val firebase_id: String = "",
    val name: String = "",
    val hosted_by: String = "",
    val datetime: Long = 0L,
    val location: String = "",
    val description: String = "",
    val updates: String = ""
)