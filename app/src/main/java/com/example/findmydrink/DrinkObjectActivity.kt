package com.example.findmydrink

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.findmydrink.databinding.ActivityDrinkObjectBinding
import com.example.findmydrink.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class DrinkObjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrinkObjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrinkObjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()

        val drinkInfo = intent.getSerializableExtra(
            "drinkKey"
        ) as DrinkObject

        if(drinkInfo.strDrinkThumb != "") {
            imageDownload(drinkInfo.strDrinkThumb)
        } else {
            Log.i("STATUS_image", binding.drinkImageImageView.toString())
        }

        binding.drinkNameTextView.setText(drinkInfo.strDrink)
        Log.i("STATUS_DRINKNAMEOBJ", drinkInfo.strDrink)
        binding.instructionTextView.setText(drinkInfo.strInstructions)
        Log.i("STATUS_strInstructions", drinkInfo.strInstructions)
        Log.i("STATUS_imageURL", drinkInfo.strDrinkThumb)
    }

    private fun imageDownload(strDrinkThumb: String) {
        var imageJob: Job? = null
        imageJob = CoroutineScope(Dispatchers.IO).launch {
            val url = URL(strDrinkThumb)
            val connection = url.openConnection() as HttpURLConnection

            var bitmap : Bitmap? = null
            try {
                connection.getInputStream().use {stream ->
                    bitmap = BitmapFactory.decodeStream(stream)
                }
            } finally {
                connection.disconnect()
            }

            withContext(Dispatchers.Main) {
                binding.drinkImageImageView.setImageBitmap(bitmap)
            }
        }
    }
}