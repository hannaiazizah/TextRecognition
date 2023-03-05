package com.hanna.textrecognition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.hanna.textrecognition.databinding.FragmentCameraBinding
import com.hanna.textrecognition.presentation.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment: Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding = _binding!!

    @Inject
    lateinit var permissionManager: PermissionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ask permission
        setupPermissionLauncher()
    }

    private fun setupPermissionLauncher() {
        permissionManager.permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { areGranted ->
            val isNotGranted = areGranted.containsValue(false)
            if (!isNotGranted) {
                permissionManager.showPermissionDialog(requireActivity())
            } else {
                startCamera()
            }
        }

        permissionManager.checkPermissionRequest(requireActivity()) {
           startCamera()
        }
    }

    private fun startCamera() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}