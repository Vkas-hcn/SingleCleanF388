package com.smoke.clears.away.single.d.c

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smoke.clears.away.single.QingSingle

class DimoKeTing:  AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, QingSingle::class.java))
        finish()
    }
}