package com.example.sehatin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.widget.Toast
import com.google.android.gms.maps.model.Marker

object Toaster {
    fun show(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
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

    fun getShopData() : ArrayList<Maps> {
        val maps = ArrayList<Maps>()

        val map =
            listOf(
                Maps(
                    0,
                    -5.378761865499519,
                    105.28868452075153,
                    "Informa Lampung",
                    "Just a coffe shop"
                ),
                Maps(
                    1,
                    -5.3700843016966004,
                    105.27385336931376,
                    "Sinar Jaya Lampung",
                    "Bus Stop"
                ),
                Maps(
                    id = 2,
                    -5.359829738720714,
                    105.25745970777072,
                    "Pasar Untung Seropati",
                    "Pasar ini adalah pasar tradisional yang ada di daerah Lampung, Pasar ini cukup terkenal karena harganya yang murah dan kualiasnya yang bagus"
                ),

            )
         maps.addAll(map)
        return maps
    }
}

data class Maps(
    val id: Int,
    val lat: Double,
    val lon: Double,
    val name: String,
    val description: String
)

