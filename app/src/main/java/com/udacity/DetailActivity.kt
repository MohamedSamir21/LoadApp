package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.udacity.MainActivity.Companion.FILENAME
import com.udacity.MainActivity.Companion.NOTIFICATION_ID
import com.udacity.MainActivity.Companion.STATUS
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        //To retrieve the fileName and the status of download and assign them to the views.
        tvDownloadedFileName.text = intent.getStringExtra(FILENAME)

        tvStatusValue.setTextColor(
            if(intent.getStringExtra(STATUS) == "Success") Color.GREEN
            else Color.RED)
        tvStatusValue.text = intent.getStringExtra(STATUS)

        okButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID )
    }

}
