package com.example.gamestoreapp

import android.text.Editable
import android.text.TextWatcher

class PhoneNumberTextWatcher : TextWatcher {
    private var isFormatting = false
    private var isDeleting = false
    private val maxRawLength = 11 // Максимальная длина без учета формата

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        isDeleting = count > after
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(editable: Editable?) {
        if (isFormatting || editable.isNullOrEmpty()) return

        val rawInput = editable.toString().replace(Regex("[^\\d]"), "")

        // Проверяем превышение длины
        if (rawInput.length > maxRawLength) {
            isFormatting = true
            // Удаляем последний введенный символ
            editable.delete(editable.length - 1, editable.length)
            isFormatting = false
            return
        }

        isFormatting = true
        try {
            // Форматируем номер
            val formatted = buildFormattedPhoneNumber(rawInput)

            // Обновляем текст
            editable.replace(0, editable.length, formatted)
        } finally {
            isFormatting = false
        }
    }

    private fun buildFormattedPhoneNumber(input: String): String {
        val builder = StringBuilder()
        if (input.isNotEmpty()) {
            builder.append("+7")
            if (input.length > 1) {
                builder.append(" (").append(input.substring(1, minOf(4, input.length)))
            }
            if (input.length > 4) {
                builder.append(") ").append(input.substring(4, minOf(7, input.length)))
            }
            if (input.length > 7) {
                builder.append("-").append(input.substring(7, minOf(9, input.length)))
            }
            if (input.length > 9) {
                builder.append("-").append(input.substring(9, input.length))
            }
        }
        return builder.toString()
    }
}
