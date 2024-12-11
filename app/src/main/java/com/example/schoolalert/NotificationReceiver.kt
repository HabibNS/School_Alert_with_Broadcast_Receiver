package com.example.schoolalert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Mendapatkan subject dari intent yang dikirim
        val subject = intent.getStringExtra("subject") ?: "Jadwal"

        // Membuat channel notifikasi (hanya untuk Android 8.0 dan yang lebih baru)
        val channelId = "school_alert_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "School Alert Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Membangun notifikasi
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icon notifikasi
            .setContentTitle("Waktu untuk: $subject")
            .setContentText("Jangan lupa melakukan aktivitas: $subject")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Menutup notifikasi setelah di-tap
            .build()

        // Menampilkan notifikasi
        notificationManager.notify(0, notification)
    }
}
