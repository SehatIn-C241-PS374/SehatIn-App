package com.example.sehatin.utils

import android.content.Context
import android.widget.Toast

object Toaster {
    fun show(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}