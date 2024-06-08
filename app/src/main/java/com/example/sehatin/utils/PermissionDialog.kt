package com.example.sehatin.utils

import android.app.AlertDialog
import androidx.fragment.app.Fragment

/**
 * This come in handy in Compose, why I'm not using Compose yet lmao
 * */

fun Fragment.showsPermission(
    title: String,
    description: String,
    positiveButtontitle: String,
    positiveButton: () -> Unit,
) {
    AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(description)
        .setPositiveButton(positiveButtontitle) {dialog, _ ->
            positiveButton()
            dialog.dismiss()
        }
        .show()
}