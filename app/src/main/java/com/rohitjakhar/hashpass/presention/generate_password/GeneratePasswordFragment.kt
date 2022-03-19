package com.rohitjakhar.hashpass.presention.generate_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rohitjakhar.hashpass.databinding.FragmentGeneratePasswordBinding
import com.rohitjakhar.hashpass.utils.PasswordGenerate
import com.rohitjakhar.hashpass.utils.copyText

class GeneratePasswordFragment : Fragment() {
    private var _binding: FragmentGeneratePasswordBinding? = null
    private val binding get() = _binding!!
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
        initClick()
    }

    private fun initClick() = binding.apply {
        btnRefreshPassword.setOnClickListener {
            if (validation()) {
                val passwordLength: Int =
                    inputPasswordLength.editText!!.text.toString().toIntOrNull() ?: 0
                val password = PasswordGenerate().generatePassword(
                    length = passwordLength,
                    includeNumbers = switchDigit.isChecked,
                    includeSymbols = switchSymbol.isChecked,
                    includeLowerCaseLetters = switchLowerLetter.isChecked,
                    includeUpperCaseLetters = switchUpperLetter.isChecked
                )
                inputPasswordCreated.editText!!.setText(password)
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
