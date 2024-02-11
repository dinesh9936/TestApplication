package com.rama.apps.testapplication.data.model


data class Video(
    val description: String = "",
    val sources: List<String> = emptyList(),
    val subtitle: String = "",
    val thumb: String = "",
    val title: String = ""
) {
    constructor() : this("", emptyList(), "", "", "")
}
