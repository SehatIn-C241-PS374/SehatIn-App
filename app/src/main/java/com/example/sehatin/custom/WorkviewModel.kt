package com.example.sehatin.custom

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WorkviewModel : ViewModel() {

    val workflowState = MutableLiveData<WorkflowState>()

    fun setWorkflowState(workflowState: WorkflowState) {
//        if (workflowState != WorkflowState.CONFIRMED &&
//            workflowState != WorkflowState.SEARCHING &&
//            workflowState != WorkflowState.SEARCHED)
//        {
//
//        }
        this.workflowState.value = workflowState
    }

    fun confirmingObject(progress: Float) {
        val isConfirmed =
            progress.compareTo(1f) == 0 // Checking the value to start the progress indicator
        if (isConfirmed) {
            setWorkflowState(WorkflowState.SEARCHING)
        } else {
            setWorkflowState(WorkflowState.CONFIRMING)
        }
    }

    enum class WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        CONFIRMED,
        SEARCHING,
        SEARCHED

    }
}