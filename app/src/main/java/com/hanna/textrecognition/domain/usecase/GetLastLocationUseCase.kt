package com.hanna.textrecognition.domain.usecase

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.hanna.textrecognition.domain.core.Either
import com.hanna.textrecognition.domain.core.Failure
import com.hanna.textrecognition.domain.core.FlowUseCase
import com.hanna.textrecognition.util.PermissionManager
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class GetLastLocationUseCase @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val permissionManager: PermissionManager
) : FlowUseCase<Location, FlowUseCase.None>() {

    @SuppressLint("MissingPermission")
    override suspend fun run(params: None): Flow<Either<Failure, Location>> {
        return callbackFlow {
            if (permissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                permissionManager.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                val cancellationTokenSource = CancellationTokenSource()
                fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        trySend(Either.success(location))
                    } else {
                        trySend(Either.fail(Failure.LocationFailure))
                    }
                }
            } else {
                trySend(Either.fail(Failure.PermissionFailure))
            }
            awaitClose { fusedLocationProviderClient.flushLocations() }

        }
    }

//    private fun startLocationUpdates() {
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                for (location in locationResult.locations){
//                    // Update UI with location data
//                    // ...
//                }
//            }
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
//            locationCallback,
//            Looper.getMainLooper())
//    }


}