package com.example.schoolalert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.widget.Toast

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            // Cek jenis koneksi
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                // Wi-Fi aktif
                Toast.makeText(context, "Wi-Fi Aktif", Toast.LENGTH_SHORT).show()
            } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                // Jaringan Seluler aktif
                Toast.makeText(context, "Jaringan Seluler Digunakan", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
        }
    }
}
