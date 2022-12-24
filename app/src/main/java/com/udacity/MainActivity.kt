package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*



class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var selectedFile: String
    private lateinit var downloadManager: DownloadManager
    private lateinit var radioGroup: RadioGroup
    private lateinit var loadingButton: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        loadingButton = findViewById(R.id.custom_button)
        radioGroup = findViewById(R.id.radioGroup)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            CHANNEL_ID,
            getString(R.string.details_notification_channel_id)
        )

        custom_button.setOnClickListener {

            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            when(selectedRadioButtonId){
                rbGlide.id->{
                    URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
                    selectedFile = getString(R.string.glide_image_loading_library_by_bumptech)
                }

                rbLoadApp.id ->{
                    URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                    selectedFile = getString(R.string.loadapp_current_repository_by_udacity)
                }

                rbRetrofit.id ->{
                    URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
                    selectedFile = getString(R.string.retrofit_type_safe_http_client_for_android_and_java_by_square_inc)
                }

                else -> {
                    Toast.makeText(applicationContext,
                    "Please, select the file to download", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            loadingButton.setLoadingButtonStatus(ButtonState.Completed)
//            Log.i("MainActivity", "Receive")
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//            Log.i("MainActivity", "$id")
//            Log.i("MainActivity", "$downloadID")

            notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager


            if (id == downloadID){
                if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
                    if(cursor.moveToFirst()){
                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            == DownloadManager.STATUS_SUCCESSFUL){
                                notificationManager.sendNotification(
                                    context.getText(R.string.notification_description).toString(),
                                    context, "Success")
                                Log.i("MainActivity", "Success")
                            }
                            else {
                                notificationManager.sendNotification(
                                    context.getText(R.string.notification_description).toString(),
                                    context, "Failed")
                                Log.i("MainActivity", "Failed")
                            }
                        }
                    }
                }
            }
        }


    private fun download() {
        loadingButton.setLoadingButtonStatus(ButtonState.Loading)
        Log.i("MainActivity", "download")
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Log.i("MainActivity", URL)

    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download_notification_channel_description)

            val notificationManager = applicationContext.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private var URL = ""
        private const val CHANNEL_ID = "channelId"
        val NOTIFICATION_ID = 0
        const val FILENAME = "fileName"
        const val STATUS = "status"
    }

    private val FLAGS = 0

    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, status: String) {

        // Create the content intent for the notification, which launches (DetailActivity)
        val intent = Intent(applicationContext, DetailActivity::class.java)
        intent.putExtra(FILENAME, selectedFile)
        intent.putExtra(STATUS, status)
        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Custom Style
        val completeDownloadImage = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.ic_assistant_black_24dp
        )
        // Adding (see changes) action.
        val detailsIntent = Intent(applicationContext, DetailActivity::class.java)
        val detailsPendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            detailsIntent,
            FLAGS)
        action = NotificationCompat.Action.Builder(0,applicationContext.getString(R.string.notification_button)
            ,detailsPendingIntent).build()

        // To support older versions, we get an instance of NotificationCompat.Builder
        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .addAction(action)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLargeIcon(completeDownloadImage)
        notify(NOTIFICATION_ID, builder.build())
    }

}
