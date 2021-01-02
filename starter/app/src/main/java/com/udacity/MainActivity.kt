package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.utils.FILENAME_EXTRA
import com.udacity.utils.NOTIFICATION_ID
import com.udacity.utils.STATUS_EXTRA
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 0

    private var selectedGitHubRepository: String? = null
    lateinit var loadingButton: LoadingButton

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // set up button
        loadingButton = findViewById(R.id.loadingButton)

        loadingButton.setOnClickListener {
            when (radioGroup.checkedRadioButtonId) {
                R.id.rb_glide -> download(URL_GLIDE)
                R.id.rb_loadApp -> download(URL_PROJECT)
                R.id.rb_retrofit -> download(URL_RETROFIT)
                else -> showToast(getString(R.string.noFileSelectedMessage))
            }
        }

        // setup Notification Manager & Create the notification channel
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(getString(R.string.githubRepo_notification_channel_id), getString(R.string.githubRepo_notification_channel_name))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent.action

            // show a toast when download completed
            if (downloadID == id){
                showToast("Download Completed")
            }

            val cursor: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            while (cursor.moveToNext()){
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        loadingButton.setLoadingButtonState(ButtonState.Completed)
                        notificationManager.sendNotification(selectedGitHubRepository.toString(), applicationContext, "Failed")
                    }

                    DownloadManager.STATUS_SUCCESSFUL -> {
                        loadingButton.setLoadingButtonState(ButtonState.Completed)
                        notificationManager.sendNotification(selectedGitHubRepository.toString(), applicationContext, "Success")
                    }
                }
            }
        }
    }

    // Get the URI from the selected git hub repository and download it otherwise provide a text that a file is not downloaded and don't call download.
    private fun download(url: String) {

        selectedGitHubRepository = url
        loadingButton.setLoadingButtonState(ButtonState.Loading)


        var file = File(getExternalFilesDir(null), "/repos")

        if (!file.exists()) {
            file.mkdirs()
        }

        val request =
            DownloadManager.Request(Uri.parse(selectedGitHubRepository))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/repos/repository.zip"
                )


        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    companion object {
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/master1.zip"
        private const val URL_PROJECT =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/master.zip"
    }

    // Disable the button while the animation is running
    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

    private fun showToast(text: String) {
        val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun createChannel(channelId: String, channelName: String) {
        // Check to see if the API Level is a API Level 26 as it requires a channel to be created
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download is done!"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

} // End of Main Activity
