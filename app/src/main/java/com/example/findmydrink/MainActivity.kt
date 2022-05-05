package com.example.findmydrink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.findmydrink.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    var drinkSelected : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Spinner
        drinkSelectSpinner()
    }

    private fun drinkSelectSpinner() {
        val drinkSelectSpinner: Spinner = findViewById(R.id.selectDrink_spinner)

        val adapter = ArrayAdapter.createFromResource(this, R.array.DrinkSelection,
            android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        drinkSelectSpinner.adapter = adapter
        drinkSelectSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            if(parent.getId() == R.id.selectDrink_spinner) {
                val text: String = parent?.getItemAtPosition(position).toString()
                drinkSelected = text
                Log.i("STATUS stateSelected Variable: ", drinkSelected)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}