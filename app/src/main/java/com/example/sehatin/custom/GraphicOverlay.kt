package com.example.sehatin.custom

import android.content.Context
import android.content.res.Configuration
import android.gesture.OrientedBoundingBox
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.camera.core.CameraSelector
import kotlin.math.ceil

/**
 * A custom view as the container to be drawn overlay of the previewView.
 * It can add any graphical objects, update the objects, and remove the objects,
 * triggering the appropriate drawing and invalidation within the view.
 *
 * Ref. MLKit Sample Code Github
 * */
class GraphicOverlay(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private val lock = Any()
    private val graphics: MutableList<Graphic> = ArrayList()
    private var mScale: Float = 0F
    private var previewWidth : Float = 0F
    private var previewHeight : Float = 0F

    abstract class Graphic(protected val overlay: GraphicOverlay) {
        protected val context : Context = overlay.context

        abstract fun draw(canvas: Canvas) // Nullable Canvas?
    }

    fun clear() {
        synchronized(lock) { graphics.clear() }
        postInvalidate()
    }

    fun add(graphic: Graphic) {
        synchronized(lock) { graphics.add(graphic) }
    }

    private fun isLandScapeMode(): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    fun setPreviewDimension(width: Int, height: Int) {
        if (isLandScapeMode()) {
            previewWidth = height.toFloat()
            previewHeight = width.toFloat()
        } else {
            previewWidth = width.toFloat()
            previewHeight = height.toFloat()
        }

    }


    fun calculateRect(overlay: GraphicOverlay, boundingBoxT: Rect): RectF {

        val scaleX = overlay.width.toFloat() / previewWidth
        val scaleY = overlay.height.toFloat() / previewHeight
        val scale = scaleX.coerceAtLeast(scaleY) // Probably same as Max(scalex, scaleY)
        mScale = scale

        // Calculate offset (we need to center the overlay on the target)
        val offsetX = (overlay.width.toFloat() - ceil(previewWidth * mScale)) / 2.0f
        val offsetY = (overlay.height.toFloat() - ceil(previewHeight * mScale)) / 2.0f

        val mappedBox = RectF().apply {
            left = boundingBoxT.left * mScale + offsetX
            top = boundingBoxT.top * mScale + offsetY
            right = boundingBoxT.right * mScale + offsetX
            bottom = boundingBoxT.bottom * mScale + offsetY
        }
        
        return mappedBox
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(lock) {
            for (graphic in graphics) {
                graphic.draw(canvas)
            }
        }
    }

}