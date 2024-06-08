package com.example.sehatin.custom

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import com.example.sehatin.R
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class ObjectDetectorProcessor(
    private val graphic: GraphicOverlay,
    private val viewModel: WorkviewModel
) : BaseImageAnalyzer<List<DetectedObject>>() {

    private val confirmationController : ObjectConfirmationController = ObjectConfirmationController(graphicOverlay)
    private val cameraReticleAnimator : CameraReticleAnimator = CameraReticleAnimator(graphicOverlay)
    private val reticleOuterRingRadius: Int = graphicOverlay
        .resources
        .getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_stroke_radius)

    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .build()

    private val detector = ObjectDetection.getClient(options)
    override val graphicOverlay: GraphicOverlay
        get() = graphic
    override fun detectInImage(image: InputImage): Task<List<DetectedObject>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: Exception) {
            Log.d("TAG", "Error: $e")
        }
    }


    override fun onFailure(e: Exception) {
        Log.w(TAG, "Face Detector failed.$e")
    }

    override fun onSuccess(
        results: List<DetectedObject>,
        graphicsOverlay: GraphicOverlay,
        bitmap: Bitmap
    ) {

        /**
         * Phase 1: Draw the object and start the reticle ui animation
         * */
        val objectIndex = 0
        val hasValidObjects = results.isNotEmpty()
        if (!hasValidObjects) {
            confirmationController.reset()
            viewModel.setWorkflowState(WorkviewModel.WorkflowState.DETECTING)
        } else {
            val visionObject = results[objectIndex]

            if (objectBoxOverlapsConfirmationReticle(graphicOverlay, visionObject)) {
                confirmationController.confirming(visionObject.trackingId)

                viewModel.confirmingObject(
                    SearchedObject(visionObject, bitmap),
                    confirmationController.progress
                )
            } else {
                // Object detected but user doesn't want to pick this one.
                confirmationController.reset()
                viewModel.setWorkflowState(WorkviewModel.WorkflowState.DETECTED)
            }
        }


        /**
         * Phase 2: Drawing the object when the animation is complete / abort if failed
         * */
        graphicsOverlay.clear()
        if (!hasValidObjects) {
            graphicsOverlay.add(ObjectReticleGraphic(graphicsOverlay, cameraReticleAnimator))
            cameraReticleAnimator.start()
        } else {
            val visionObject = results[objectIndex]
            if (objectBoxOverlapsConfirmationReticle(graphicsOverlay, visionObject)) {

                // User is confirming the object selection.
                cameraReticleAnimator.cancel()
                graphicsOverlay.add(
                    ObjectDetectorGraphic(
                        graphicsOverlay,
                        visionObject,
                        confirmationController
                    )
                )
                if (!confirmationController.isConfirmed) {
                    // Shows a loading indicator to visualize the confirming progress if in auto search mode.
                    graphicsOverlay.add(
                        ObjectConfirmationGraphic(
                            graphicsOverlay,
                            confirmationController
                        )
                    )
                }
            } else {
                // Object is detected but the confirmation reticle is moved off the object box, which
                // indicates user is not trying to pick this object.
                graphicsOverlay.add(
                    ObjectDetectorGraphic(
                        graphicsOverlay, results[0], confirmationController
                    )
                )
                graphicsOverlay.add(
                    ObjectReticleGraphic(
                        graphicOverlay, cameraReticleAnimator
                    )
                )
                cameraReticleAnimator.start()
            }
        }

        graphicsOverlay.invalidate()

    }


    private fun objectBoxOverlapsConfirmationReticle(
        graphicOverlay: GraphicOverlay,
        visionObject: DetectedObject,
    ) : Boolean {
        val boxRect = graphicOverlay.calculateRect(
            graphicOverlay,
            visionObject.boundingBox
        )
        val reticleCenterX = graphicOverlay.width / 2f
        val reticleCenterY = graphicOverlay.height / 2f
        val reticleRect = RectF (
            reticleCenterX - reticleOuterRingRadius,
            reticleCenterY - reticleOuterRingRadius,
            reticleCenterX + reticleOuterRingRadius,
            reticleCenterY + reticleOuterRingRadius
        )

        Log.d(TAG, "boxRect: $boxRect")
        Log.d(TAG, "reticleRect: $reticleRect")

        return reticleRect.intersect(boxRect)
    }

    companion object {
        private const val TAG = "Object Detector Processor"
    }

}