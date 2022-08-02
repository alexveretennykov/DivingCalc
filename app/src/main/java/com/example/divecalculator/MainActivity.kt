package com.example.divecalculator

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.divecalculator.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds

class MainActivity : FragmentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}
    }

}