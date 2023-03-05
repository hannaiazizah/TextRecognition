package com.hanna.textrecognition.presentation.preview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hanna.textrecognition.R
import com.hanna.textrecognition.data.core.onFailure
import com.hanna.textrecognition.data.core.onSuccess
import com.hanna.textrecognition.databinding.FragmentPreviewBinding
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
                        it?.let { binding.ivPreviewImage.setImageURI(it) }
                    }
                }
                launch {
                    viewModel.visionResult.collectLatest {
                        it.onSuccess { visionText ->
                            binding.tvPreviewTextResult.text = visionText?.text
                        }
                        it.onFailure {
                            binding.tvPreviewTextResult.text =
                                getString(R.string.label_failed_text_recognition)
                        }

                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}