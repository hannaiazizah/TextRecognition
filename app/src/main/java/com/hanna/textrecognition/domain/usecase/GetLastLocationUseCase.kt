package com.hanna.textrecognition.domain.usecase

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
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
    override suspend fun run(params: None): Flow<Location?> {
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
                        trySend(location)
                    } else {
                        trySend(null)
                    }
                }
            } else {
                trySend(null)
            }
            awaitClose { fusedLocationProviderClient.flushLocations() }

        }
    }


}