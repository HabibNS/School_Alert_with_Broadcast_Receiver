package com.example.schoolalert

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScheduleAdapter

    companion object {
        const val REQUEST_CODE_ACCESS_NETWORK_STATE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menambahkan izin untuk notifikasi pada Android 13 ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Meminta izin akses jaringan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                REQUEST_CODE_ACCESS_NETWORK_STATE
            )
        }

        val etSubject = findViewById<EditText>(R.id.etSubject)
        val etTime = findViewById<EditText>(R.id.etTime)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        recyclerView = findViewById(R.id.recyclerView)

        dbHelper = DatabaseHelper(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ScheduleAdapter(dbHelper.getAllSchedules())
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            val subject = etSubject.text.toString()
            val time = etTime.text.toString()

            if (subject.isNotEmpty() && time.isNotEmpty()) {
                dbHelper.insertSchedule(subject, time)
                adapter.updateList(dbHelper.getAllSchedules())
                setNotification(subject, time)
                Toast.makeText(this, "Jadwal ditambahkan!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Isi semua kolom!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menangani izin POST_NOTIFICATIONS pada Android 13 ke atas
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    // Menangani hasil permintaan izin akses jaringan
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_ACCESS_NETWORK_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Izin diberikan, Anda dapat melanjutkan untuk memeriksa status jaringan
                    Toast.makeText(this, "Izin akses jaringan diberikan", Toast.LENGTH_SHORT).show()
                } else {
                    // Izin ditolak, beri tahu pengguna
                    Toast.makeText(this, "Izin akses jaringan ditolak", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setNotification(subject: String, time: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("subject", subject)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance()
        val timeParts = time.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        calendar.set(Calendar.MINUTE, timeParts[1].toInt())

        // Menjadwalkan alarm untuk memunculkan notifikasi pada waktu yang ditentukan
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
