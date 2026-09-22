package com.arijit.torchbutton

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var torchOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = findFlashCamera()
        toggleTorch()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        toggleTorch()
    }

    private fun findFlashCamera(): String? {
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: Exception) { null }
    }

    private fun toggleTorch() {
        val id = cameraId ?: return

        try {
            torchOn = !torchOn
            cameraManager.setTorchMode(id, torchOn)

            if (torchOn) {
                window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

        } catch (e: Exception) { torchOn = false }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (torchOn) {
            try {
                cameraId?.let { cameraManager.setTorchMode(it, false) }
            } catch (_: Exception) { }
        }
    }
}