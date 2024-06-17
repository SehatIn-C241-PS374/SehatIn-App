package com.example.sehatin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.amulyakhare.textdrawable.TextDrawable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object Toaster {
    fun show(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

object Colors {
    private val colorLists = arrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA)

    fun getRandomColor() : Int {
        val randomIndex = (Math.random() * colorLists.size).toInt()
        return colorLists[randomIndex]
    }

}

/**
 * The color from the Color enum classes and the Shape are from the TextDrawable enum classes
 * */
fun getTextDrawable(color: Int, shape: Int, letter: String) : TextDrawable {

    return TextDrawable.Builder()
        .setColor(color)
        .setShape(shape)
        .setText(letter)
        .build()
}

fun Float.format() : String {
    val decimals = "%.2f"
    return decimals.format(this)
}



object Utils {
    fun getCornerRoundedBitmap(imageBitmap: Bitmap, objectThumbnailCornerRadius: Int): Bitmap {
        val dstBitmap =
            Bitmap.createBitmap(imageBitmap.width, imageBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dstBitmap)
        val paint = Paint().apply {
            isAntiAlias = true
        }

        val rectF = RectF(0f, 0f, imageBitmap.width.toFloat(), imageBitmap.height.toFloat())
        canvas.drawRoundRect(
            rectF,
            objectThumbnailCornerRadius.toFloat(),
            objectThumbnailCornerRadius.toFloat(),
            paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(imageBitmap, 0f, 0f, paint)
        return dstBitmap
    }
}

fun parseIdFromUrl(url : String) : String {
    val urlParts = url.split("/")
    val recipeId = urlParts[6].split("?")
    return recipeId[0]
}

 fun randomSearch() : String {
    return key_search.random()
}

fun formatCurrentDate(): String {

    val today = Date()
    return DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(today)

}