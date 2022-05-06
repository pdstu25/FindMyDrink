package com.example.findmydrink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findmydrink.databinding.ActivityDrinkObjectBinding
import com.example.findmydrink.databinding.ActivityMainBinding

class DrinkObjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrinkObjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrinkObjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}