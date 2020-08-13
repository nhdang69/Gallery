package com.example.gallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_photos ->{
                    true
                }
                R.id.action_albums ->{
                    true
                }
                R.id.action_videos ->{
                    true
                }else -> false
            }
        }
    }
}