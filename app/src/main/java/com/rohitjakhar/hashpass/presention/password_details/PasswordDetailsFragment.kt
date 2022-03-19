package com.rohitjakhar.hashpass.presention.password_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rohitjakhar.hashpass.databinding.FragmentPasswordDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordDetailsFragment : Fragment() {

    private var _binding: FragmentPasswordDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
