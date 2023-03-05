package com.hanna.textrecognition.data.repository

interface DistanceRepository {
    fun calculateDistance(lat: Double, long: Double)
}