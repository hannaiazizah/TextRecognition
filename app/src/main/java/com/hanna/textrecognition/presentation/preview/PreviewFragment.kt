package com.hanna.textrecognition.presentation.preview

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hanna.textrecognition.R
import com.hanna.textrecognition.databinding.FragmentPreviewBinding
import com.hanna.textrecognition.domain.core.Failure
import com.hanna.textrecognition.domain.core.onFailure
import com.hanna.textrecognition.domain.core.onSuccess
import com.hanna.textrecognition.domain.model.DistanceUiModel
import com.hanna.textrecognition.domain.model.ImageAttributesUiModel
import com.hanna.textrecognition.presentation.camera.CameraViewModel
import com.hanna.textrecognition.util.asFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
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

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFlow()
        setupButtonListener()
    }

    private fun setupButtonListener() {
        binding.btnTryAgain.setOnClickListener{
            viewModel.clearData()
            findNavController().navigateUp()
        }
        binding.btnTryAgain.setOnClickListener {
            viewModel.postUpdateData()
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.imageUri.collectLatest {
                        it?.let {
                            binding.ivPreviewImage.setImageURI(it)
                        }
                    }
                }
                launch {
                    viewModel.visionResult.collectLatest {
                        if (it != null) {
                            binding.tvPreviewTextVisionResult.setText(it.text)
                        } else {
                            binding.tvPreviewTextVisionResult.setText(
                                getString(R.string.label_failed_text_recognition)
                            )
                        }
                    }
                }
                launch {
                    viewModel.locationResult.collectLatest { location ->
                        if (location == null) {
                            setLocationError()
                        } else {
                            setLocationSuccess(location)
                        }
                    }
                }
                launch {
                    viewModel.distanceResult.collectLatest {
                        it.onFailure { setDistanceErrorView() }
                        it.onSuccess { result ->
                            setDistanceSuccessView(result)
                        }
                    }
                }
                launch {
                    viewModel.updateResult.collectLatest {
                        it.onSuccess {
                            Toast.makeText(requireContext(), "Update data success!", Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            Toast.makeText(requireContext(), "Update data failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                launch {
                    observeTextField()
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private suspend fun observeTextField() = with(binding) {
        val textFlow = tvPreviewTextVisionResult.asFlow()
            .debounce(DEBOUNCE_MILLIS)
            .filterNotNull()
            .map {
                it.toString()
            }

        val latFlow = tvLocationLatitude.asFlow()
            .debounce(DEBOUNCE_MILLIS)
            .filterNotNull()
            .map {
                it.toString().replace(",", ".").toDouble()
            }

        val longFlow = tvLocationLongitude.asFlow()
            .debounce(DEBOUNCE_MILLIS)
            .filterNotNull()
            .map {
                it.toString().replace(",", ".").toDouble()
            }

        val distanceFlow = tvDistanceValue.asFlow()
            .debounce(DEBOUNCE_MILLIS)
            .filterNotNull()
            .map {
                it.toString()
            }

        val timeFlow = tvEstimatedTimeValue.asFlow()
            .debounce(DEBOUNCE_MILLIS)
            .filterNotNull()
            .map {
                it.toString()
            }

        combine(textFlow, latFlow, longFlow, distanceFlow, timeFlow) { text, lat, long, distance, time ->
            ImageAttributesUiModel(
                text = text,
                lat = lat,
                long = long,
                distance = distance,
                time = time
            )
        }.collectLatest {
            viewModel.updateData(it)
            binding.btnUpdateData.isEnabled = true
        }
    }

    private fun setLocationError(failure: Failure? = null) {
        binding.tvLocationLatitude.setText("0")
        binding.tvLocationLongitude.setText("0")
    }

    private fun setLocationSuccess(location: Location) {
        binding.tvLocationLatitude.setText(location.latitude.toString())
        binding.tvLocationLongitude.setText(location.longitude.toString())
    }

    private fun setDistanceErrorView() {
        binding.tvDistanceValue.setText("0")
        binding.tvEstimatedTimeValue.setText("0")
    }

    private fun setDistanceSuccessView(model: DistanceUiModel) {
        binding.tvDistanceValue.setText(model.distance)
        binding.tvEstimatedTimeValue.setText(model.estimatedTime)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEBOUNCE_MILLIS = 100L
    }


}