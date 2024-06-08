package com.example.sehatin.custom

import android.os.CountDownTimer

/**
 * Controls the progress of object confirmation before performing additional operation on the
 * detected object.
 */
internal class ObjectConfirmationController
    /**
     * @param graphicOverlay Used to refresh camera overlay when the confirmation progress updates.
     */
    (graphicOverlay: GraphicOverlay) {

    private val countDownTimer : CountDownTimer

    private var objectId: Int? = null

    var progress = 0f
        private set

    val isConfirmed : Boolean
        get() = progress.compareTo(1f) == 0

    init {
        /**
         * This time are setting dynamically with the setting pref to handle both cases, the hell
         */
        val confirmationTimeMs = 1500.toLong()
        countDownTimer = object : CountDownTimer(confirmationTimeMs, 20) {
            override fun onTick(millisUntilFinished: Long) {
                progress = (confirmationTimeMs - millisUntilFinished).toFloat() / confirmationTimeMs
                graphicOverlay.invalidate()
            }

            override fun onFinish() {
                progress = 1f
            }
        }
    }

    fun confirming(objectId: Int?) {
        if (objectId == this.objectId) {
            // do nothing
            return
        }

        reset()
        this.objectId = objectId
        countDownTimer.start()
    }

    fun reset() {
        countDownTimer.cancel()
        progress = 0f
        objectId = null
    }

}