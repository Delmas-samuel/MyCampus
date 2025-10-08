package com.example.mycampus.data.remote.model

data class OrsDirectionsResponse(
    val features: List<Feature>
)

data class Feature(
    val geometry: Geometry
)

data class Geometry(
    val coordinates: List<List<Double>>
)
