package com.hanna.textrecognition.presentation.preview

import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.mlkit.vision.common.InputImage
import com.hanna.textrecognition.R
import com.hanna.textrecognition.domain.core.onFailure
import com.hanna.textrecognition.domain.core.onSuccess
import com.hanna.textrecognition.databinding.FragmentPreviewBinding
import com.hanna.textrecognition.domain.core.Failure
import com.hanna.textrecognition.presentation.camera.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreviewFragment : Fragment() {
    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<CameraViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFlow()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.imageUri.collectLatest {
                        it?.let {
                            binding.ivPreviewImage.setImageURI(it)
                            runTextRecognition(it)
                        }
                    }
                }
                launch {
                    viewModel.visionResult.collectLatest {
                        it.onSuccess { visionText ->
                            binding.tvPreviewTextVisionResult.text = visionText?.text

                        }
                        it.onFailure {
                            binding.tvPreviewTextVisionResult.text =
                                getString(R.string.label_failed_text_recognition)
                        }
                    }
                }
                launch {
                    viewModel.locationResult.collectLatest {
                        it.onFailure {
                            setLocationError(it)
                        }
                        it.onSuccess { location ->
                            if (location == null) {
                                setLocationError()
                            } else {
                                setLocationSuccess(location)
                            }

                        }
                    }
                }
            }
        }
    }

    private fun runTextRecognition(uri: Uri) {
        try {
            val image = InputImage.fromFilePath(requireContext(), uri)
            viewModel.runTextRecognition(image)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setLocationError(failure: Failure? = null) {
        val errorMessage = when(failure) {
            is Failure.PermissionFailure -> getString(R.string.label_error_location_permission_is_required)
            is Failure.LocationFailure -> getString(R.string.label_error_get_location)
            else -> getString(R.string.label_error_location_not_found)
        }
        binding.tvLocationLatitude.text = errorMessage
        binding.tvLocationLongitude.isVisible = false
    }

    private fun setLocationSuccess(location: Location) {
        binding.tvLocationLatitude.text = "Latitude: ${location.latitude}"
        binding.tvLocationLongitude.isVisible = true
        binding.tvLocationLongitude.text = "Longitude: ${location.longitude}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}