package com.example.sehatin.custom

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

    override fun onSuccess(results: List<DetectedObject>, graphicsOverlay: GraphicOverlay, cropRect: Rect) {
//        graphicsOverlay.clear()
//        results.forEach {
//            val objectOverlay = ObjectDetectorGraphic(graphicsOverlay, it, cropRect)
//            graphicsOverlay.add(objectOverlay)
//        }
//    }
        var objects = results
        Log.d(TAG, objects.toString())

        val objectIndex = 0
        // Handle for DetectedObjectInfo but later it seems
        val hasValidObjects = objects.isNotEmpty()
        if (!hasValidObjects) {
            confirmationController.reset()
            viewModel.setWorkflowState(WorkviewModel.WorkflowState.DETECTING)
        } else {
            val visionObject = objects[objectIndex]
            val reticleOverlap = objectBoxOverlapsConfirmationReticle(graphicOverlay, visionObject, cropRect)
            Log.d(TAG, "Reticle overlap: $reticleOverlap")

            if (reticleOverlap) {
                confirmationController.confirming(visionObject.trackingId)

                Log.d(TAG, "Reticle are ovelap")
                /** probably cover update this with the DetectedObjectInfo **/
                viewModel.confirmingObject(
                    confirmationController.progress
                )
            } else {
                // Object detected but user doesn't want to pick this one.
                confirmationController.reset()
                viewModel.setWorkflowState(WorkviewModel.WorkflowState.DETECTED)
            }
        }

        graphicsOverlay.clear()
        if (!hasValidObjects) {
            graphicsOverlay.add(ObjectReticleGraphic(graphicsOverlay, cameraReticleAnimator))
            cameraReticleAnimator.start()
        } else {
            val visionObject = objects[objectIndex]
            if (objectBoxOverlapsConfirmationReticle(graphicsOverlay, visionObject, cropRect)) {

                // User is confirming the object selection.
                cameraReticleAnimator.cancel()
                graphicsOverlay.add(
                    ObjectDetectorGraphic(graphicsOverlay, visionObject, cropRect, confirmationController)
                )
                if (!confirmationController.isConfirmed) {
                    // Shows a loading indicator to visualize the confirming progress if in auto search mode.
                    graphicsOverlay.add(ObjectConfirmationGraphic(graphicsOverlay, confirmationController))
                }
            } else {
                // Object is detected but the confirmation reticle is moved off the object box, which
                // indicates user is not trying to pick this object.
                graphicsOverlay.add(
                    ObjectDetectorGraphic(
                        graphicsOverlay, objects[0], cropRect, confirmationController
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
        cropRect: Rect
    ) : Boolean {
        val boxRect = graphicOverlay.calculateRect(
            graphicOverlay,
            cropRect.height().toFloat(),
            cropRect.width().toFloat(),
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


    //    private val options = ObjectDetectorOptions.Builder()
//        .apply {
//            setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//            enableClassification()
//        }.build()
//
//    private val detector = ObjectDetection.getClient(options)
//    override val graphicOverlay: GraphicOverlay
//        get() = view
//
//    override fun detectInImage(image: InputImage): Task<List<DetectedObject>> {
//        return detector.process(image)
//    }
//
//    override fun stop() {
//        detector.close()
//    }
//
//    override fun onSuccess(
//        result: List<DetectedObject>,
//        graphicsOverlay: GraphicOverlay,
//        rect: Rect
//    ) {
//        Log.d("ObjectDetectorProcessor", "On Success")
//        graphicOverlay.clear()
//        result.forEach {
//            val objectDetection = ObjectDetectorGraphic(graphicsOverlay, it)
//        graphicsOverlay.add(objectDetection)
//        }
//        graphicOverlay.postInvalidate()
//    }
//
//    override fun onFailure(e: Exception) {
//     Log.d("Object Detector Processor", e.toString())
//    }
}