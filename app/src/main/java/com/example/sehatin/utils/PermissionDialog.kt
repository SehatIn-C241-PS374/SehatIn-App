package com.example.sehatin.utils

import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
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

fun Fragment.checkSelfPermission(permission: Array<String>): Boolean {
    permission.forEach {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        ) return false
    }
    return true
}
