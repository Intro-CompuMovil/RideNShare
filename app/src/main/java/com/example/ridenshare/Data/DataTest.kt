package com.example.ridenshare.Data

class DataTest {
    data class Point(val latitude: Double, val longitude: Double)
    data class Route(var titulo: String, val points: MutableList<Point>)


    companion object{
        val routes: MutableList<Route> = mutableListOf()
    }
}