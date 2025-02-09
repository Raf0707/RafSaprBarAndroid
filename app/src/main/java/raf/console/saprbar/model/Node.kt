package raf.console.saprbar.model

data class Node(
    var x: Double = 0.0,
    var force: Double = 0.0,
    var displacement: Double? = null,
    var support: Boolean = false
)