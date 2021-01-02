package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.utils.FILENAME_EXTRA
import com.udacity.utils.STATUS_EXTRA
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    private var fileName = ""
    private var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        backButton.setOnClickListener {
            returnToMainActivity()
        }

        fileName = intent.getStringExtra(FILENAME_EXTRA).toString()
        status = intent.getStringExtra(STATUS_EXTRA).toString()
        tv_fileName.text = fileName
        tv_status.text = status
    }

    fun returnToMainActivity() {
        val  intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
