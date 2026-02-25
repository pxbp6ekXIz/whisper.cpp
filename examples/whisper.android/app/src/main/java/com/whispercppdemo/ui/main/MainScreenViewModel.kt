package com.whispercppdemo.ui.main

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.whispercpp.whisper.WhisperContext
import com.whispercppdemo.recorder.Recorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

private const val LOG_TAG = "MainScreenViewModel"

class MainScreenViewModel(private val application: Application) : ViewModel() {
    var canTranscribe by mutableStateOf(false)
        private set
    var statusMessage by mutableStateOf("Initializing...")
        private set
    var transcript by mutableStateOf("")
        private set
    var isRecording by mutableStateOf(false)
        private set

    private var recorder: Recorder = Recorder()
    private var whisperContext: WhisperContext? = null
    private var recordedFile: File? = null

    init {
        viewModelScope.launch {
            loadModel()
        }
    }

    private suspend fun loadModel() {
        statusMessage = "Loading offline model..."
        canTranscribe = false
        try {
            val models = application.assets.list("models")?.filter { it.endsWith(".bin") }.orEmpty()
            require(models.isNotEmpty()) {
                "No model found in app/src/main/assets/models. Add a .bin Whisper model before building."
            }
            val modelAssetPath = "models/${models.first()}"
            whisperContext = WhisperContext.createContextFromAsset(application.assets, modelAssetPath)
            canTranscribe = true
            statusMessage = "Ready"
        } catch (e: Exception) {
            Log.w(LOG_TAG, e)
            statusMessage = e.localizedMessage ?: "Failed to load model"
        }
    }

    fun toggleRecord() = viewModelScope.launch {
        try {
            if (isRecording) {
                recorder.stopRecording()
                isRecording = false
                recordedFile?.let { transcribeAudio(it) }
            } else {
                if (!canTranscribe) {
                    return@launch
                }
                statusMessage = "Recording... tap again to stop"
                val file = getTempFileForRecording()
                recorder.startRecording(file) { e ->
                    viewModelScope.launch {
                        statusMessage = e.localizedMessage ?: "Recording failed"
                        isRecording = false
                    }
                }
                isRecording = true
                recordedFile = file
            }
        } catch (e: Exception) {
            Log.w(LOG_TAG, e)
            statusMessage = e.localizedMessage ?: "Recording failed"
            isRecording = false
        }
    }

    private suspend fun transcribeAudio(file: File) {
        if (!canTranscribe) {
            return
        }

        canTranscribe = false
        statusMessage = "Transcribing..."

        try {
            val data = withContext(Dispatchers.IO) { com.whispercppdemo.media.decodeWaveFile(file) }
            val start = System.currentTimeMillis()
            transcript = whisperContext?.transcribeData(data).orEmpty().trim()
            val elapsed = System.currentTimeMillis() - start
            statusMessage = "Done in ${elapsed} ms"
        } catch (e: Exception) {
            Log.w(LOG_TAG, e)
            statusMessage = e.localizedMessage ?: "Transcription failed"
        }

        canTranscribe = true
    }

    private suspend fun getTempFileForRecording() = withContext(Dispatchers.IO) {
        File.createTempFile("recording", ".wav", application.cacheDir)
    }

    override fun onCleared() {
        runBlocking {
            whisperContext?.release()
            whisperContext = null
        }
    }

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                MainScreenViewModel(application)
            }
        }
    }
}
