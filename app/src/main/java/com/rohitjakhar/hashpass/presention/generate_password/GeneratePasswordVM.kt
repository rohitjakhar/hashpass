package com.rohitjakhar.hashpass.presention.generate_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratePasswordVM @Inject constructor() : ViewModel() {
    var isFirstTimePassword: MutableStateFlow<Boolean> = MutableStateFlow(true)
        private set

    fun changeFirstTime(isFirstTime: Boolean) {
        viewModelScope.launch {
            isFirstTimePassword.emit(isFirstTime)
        }
    }

    fun generatePassword(
        length: Int,
        includeUpperCaseLetters: Boolean,
        includeLowerCaseLetters: Boolean,
        includeSymbols: Boolean,
        includeNumbers: Boolean
    ): String {
        var password = ""
        val list = ArrayList<Int>()
        if (includeUpperCaseLetters)
            list.add(0)
        if (includeLowerCaseLetters)
            list.add(1)
        if (includeNumbers)
            list.add(2)
        if (includeSymbols)
            list.add(3)

        for (i in 1..length) {
            when (list.random()) {
                0 -> password += ('A'..'Z').random().toString()
                1 -> password += ('a'..'z').random().toString()
                2 -> password += ('0'..'9').random().toString()
                3 -> password += listOf(
                    '!',
                    '@',
                    '#',
                    '$',
                    '%',
                    '&',
                    '*',
                    '+',
                    '=',
                    '-',
                    '~',
                    '?',
                    '/',
                    '_'
                ).random().toString()
            }
        }
        return password
    }
}
