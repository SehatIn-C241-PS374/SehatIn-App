package com.example.sehatin.custom

import android.content.Context
import android.content.res.Configuration
import android.gesture.OrientedBoundingBox
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
    private var mScale: Float? = null
    private var mOffsetX: Float? = null
    private var mOffsetY: Float? = null

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

    fun calculateRect(overlay: GraphicOverlay, height: Float, width: Float, boundingBoxT: Rect): RectF {

        // for land scape
        fun isLandScapeMode(): Boolean {
            return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }

        fun whenLandScapeModeWidth(): Float {
            return when(isLandScapeMode()) {
                true -> width
                false -> height
            }
        }

        fun whenLandScapeModeHeight(): Float {
            return when(isLandScapeMode()) {
                true -> height
                false -> width
            }
        }

        val scaleX = overlay.width.toFloat() / whenLandScapeModeWidth()
        val scaleY = overlay.height.toFloat() / whenLandScapeModeHeight()
        val scale = scaleX.coerceAtLeast(scaleY)
        overlay.mScale = scale

        // Calculate offset (we need to center the overlay on the target)
        val offsetX = (overlay.width.toFloat() - ceil(whenLandScapeModeWidth() * scale)) / 2.0f
        val offsetY = (overlay.height.toFloat() - ceil(whenLandScapeModeHeight() * scale)) / 2.0f

        overlay.mOffsetX = offsetX
        overlay.mOffsetY = offsetY

        // Bjir salah dikit ga ngaruh tadi left, right = right, left
        val mappedBox = RectF().apply {
            left = boundingBoxT.left * scale + offsetX
            top = boundingBoxT.top * scale + offsetY
            right = boundingBoxT.right * scale + offsetX
            bottom = boundingBoxT.bottom * scale + offsetY
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