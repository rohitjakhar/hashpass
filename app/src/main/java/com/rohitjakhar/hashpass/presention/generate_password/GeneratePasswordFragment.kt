package com.rohitjakhar.hashpass.presention.generate_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rohitjakhar.hashpass.R
import com.rohitjakhar.hashpass.databinding.FragmentGeneratePasswordBinding
import com.rohitjakhar.hashpass.utils.copyText
import com.rohitjakhar.hashpass.utils.loadingView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GeneratePasswordFragment : Fragment() {
    private var _binding: FragmentGeneratePasswordBinding? = null
    private val binding get() = _binding!!
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }
    private val viewModel by viewModels<GeneratePasswordVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneratePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFirstTime()
        initClick()
    }

    private fun collectFirstTime() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isFirstTimePassword.collectLatest {
                if (it) {
                    binding.btnRefreshPassword.text = "Create Password"
                } else {
                    binding.btnRefreshPassword.text = resources.getString(R.string.refresh_password)
                }
            }
        }
    }

    private fun initClick() = binding.apply {
        btnRefreshPassword.setOnClickListener {
            loadingView.show()
            if (validation()) {
                viewModel.changeFirstTime(false)
                val passwordLength: Int =
                    inputPasswordLength.editText!!.text.toString().toIntOrNull() ?: 0
                val password = viewModel.generatePassword(
                    length = passwordLength,
                    includeNumbers = switchDigit.isChecked,
                    includeSymbols = switchSymbol.isChecked,
                    includeLowerCaseLetters = switchLowerLetter.isChecked,
                    includeUpperCaseLetters = switchUpperLetter.isChecked
                )
                inputPasswordCreated.editText!!.setText(password)
                loadingView.hide()
            }
        }

        btnCopyPassword.setOnClickListener {
            copyText(
                binding.inputPasswordCreated.editText!!.text.toString()
            )
        }
    }

    private fun validation(): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
