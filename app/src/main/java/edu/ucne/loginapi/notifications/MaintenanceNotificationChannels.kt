package edu.ucne.loginapi.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object MaintenanceNotificationChannels {

    const val CHANNEL_GENERAL = "maintenance_general"
    const val CHANNEL_HIGH = "maintenance_high"
    const val CHANNEL_CRITICAL = "maintenance_critical"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val general = NotificationChannel(
            CHANNEL_GENERAL,
            "Mantenimiento general",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Recordatorios y tareas próximas de mantenimiento"
        }

        val high = NotificationChannel(
            CHANNEL_HIGH,
            "Mantenimiento importante",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alertas de mantenimiento de alta prioridad"
        }

        val critical = NotificationChannel(
            CHANNEL_CRITICAL,
            "Mantenimiento crítico",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Fallos críticos o mantenimientos muy urgentes"
        }

        manager.createNotificationChannels(listOf(general, high, critical))
    }

}
