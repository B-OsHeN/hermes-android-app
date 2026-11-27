package com.hermesagent.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object FastPathCommands {
    fun handle(context: Context, command: String): Boolean {
        val lower = command.lowercase().trim()
        return when {
            lower.startsWith("flashlight on") -> { toggleFlashlight(context, true); true }
            lower.startsWith("flashlight off") -> { toggleFlashlight(context, false); true }
            lower.startsWith("open ") -> { openApp(context, lower.removePrefix("open ").trim()); true }
            lower.startsWith("call ") -> { dial(context, lower.removePrefix("call ").trim()); true }
            lower.startsWith("text ") || lower.startsWith("sms ") -> { sms(context, lower.substringAfter(" ").trim()); true }
            else -> false
        }
    }

    private fun toggleFlashlight(context: Context, on: Boolean) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } ?: return
        cameraManager.setTorchMode(cameraId, on)
    }

    private fun openApp(context: Context, name: String) {
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(name)
            ?: pm.getInstalledApplications(0).firstOrNull { it.loadLabel(pm).toString().equals(name, true) }?.let { pm.getLaunchIntentForPackage(it.packageName) }
        intent?.let { context.startActivity(it) }
    }

    private fun dial(context: Context, number: String) {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
    }

    private fun sms(context: Context, text: String) {
        context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")).apply {
            putExtra("sms_body", text)
        })
    }
}