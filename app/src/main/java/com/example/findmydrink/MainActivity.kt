package com.example.findmydrink

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findmydrink.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    var drinkSelected: String = ""
    var drinkID : String = ""
    private val DRINKNAMES = mutableListOf<DrinkObject>()
    private val DRINKID = mutableListOf<String>()
    private val DRINKOBJ = mutableListOf<DrinkObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)

        binding.findDrinkButton.setOnClickListener(DownloadListener())

        //RecyclerView
        binding.drinkNameRecyclerView.setLayoutManager(layoutManager)
        binding.drinkNameRecyclerView.setHasFixedSize(true)
        val divider = DividerItemDecoration(applicationContext, layoutManager.orientation)
        binding.drinkNameRecyclerView.addItemDecoration(divider)

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
            if (parent.getId() == R.id.selectDrink_spinner) {
                val text: String = parent?.getItemAtPosition(position).toString()
                drinkSelected = text
                Log.i("STATUS drinkSelected Variable: ", drinkSelected)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun isNetworkAvailable(): Boolean {
        var available = false

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        cm.run {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // code for newer versions
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    ) {
                        available = true
                    }
                }

            } else {

                // code for older versions
                cm.getActiveNetworkInfo()?.run {
                    if (type == ConnectivityManager.TYPE_MOBILE
                        || type == ConnectivityManager.TYPE_WIFI
                        || type == ConnectivityManager.TYPE_VPN
                    ) {
                        available = true
                    }
                }

            }
        }

        return available
    }

    //Menu functions
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about_menuItem) {
            val builder = AlertDialog.Builder(binding.root.context)

            builder.setTitle(R.string.aboutTitle)
            builder.setMessage(R.string.aboutText)
            builder.setPositiveButton(android.R.string.ok, null)
            builder.show()

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyViewHolder(val view: View) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {

        init {
            view.findViewById<View>(R.id.item_constraintLayout)
                .setOnClickListener(this)
        }

        fun setTitle(title: DrinkObject) {
            view.findViewById<TextView>(R.id.textView_RecycleView).setText(title.strDrink)
        }

        override fun onClick(p0: View?) {
            if (p0 != null) {
                val intent = Intent(view.context, DrinkObjectActivity::class.java)
                val applyDrinkObjects = DRINKNAMES[adapterPosition]
                intent.putExtra(
                    "drinkKey",
                    applyDrinkObjects
                )
                startActivity(intent)
            }
        }

        /*override fun onClick(p0: View?) {
            if (p0 != null) {
                val intent = Intent(view.context, ArtObjectActivity::class.java)
                val applyArtObjects = METOBJECTSTRING[adapterPosition]
                intent.putExtra(
                    "artKey",
                    applyArtObjects
                )
                startActivity(intent)
            }
        }*/
    }

    inner class MyAdapter() : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.setTitle(DRINKNAMES[position])
        }

        override fun getItemCount(): Int {
            return DRINKNAMES.size
        }
    }

    private var downloadJob: Job? = null

    var urlPath = ""

    inner class DownloadListener : View.OnClickListener {
        override fun onClick(view: View?) {
            Log.i("STATUS_STDL", drinkSelected)
            if (drinkSelected.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Input required", Toast.LENGTH_SHORT
                ).show()
            } else if (isNetworkAvailable()) {
                //https://www.thecocktaildb.com/api/json/v1/1/random.php
                if (downloadJob?.isActive != true) {
                    setDownloadLink()
                    startDownload()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Network not available", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setDownloadLink(): String {
        if(drinkSelected.equals("Random")) {
            val randomDrinkURL = Uri.Builder()
                .scheme("https")
                .authority("thecocktaildb.com")
                .path("/api/json/v1/1/random.php")
            urlPath = randomDrinkURL.build().toString()
        } else if (drinkSelected.equals("Ordinary Drink")) {
            val randomDrinkURL = Uri.Builder()
                .scheme("https")
                .authority("thecocktaildb.com")
                .path("/api/json/v1/1/filter.php")
                .appendQueryParameter("c", "Ordinary_Drink")
            urlPath = randomDrinkURL.build().toString()
        } else {
            val randomDrinkURL = Uri.Builder()
                .scheme("https")
                .authority("thecocktaildb.com")
                .path("/api/json/v1/1/filter.php")
                .appendQueryParameter("c", drinkSelected)
            urlPath = randomDrinkURL.build().toString()
        }

        return urlPath
    }

    private fun startDownload() {
        DRINKNAMES.clear()
        downloadJob = CoroutineScope(Dispatchers.IO).launch {
            val searchUrl = URL(urlPath)
            val connection: HttpURLConnection = searchUrl.openConnection() as HttpURLConnection

            var jsonSearchStr = ""

            try {
                jsonSearchStr = connection.getInputStream()
                    .bufferedReader().use(BufferedReader::readText)
            } finally {
                connection.disconnect()
            }

            //nested json approach
            val jsonObject = JSONTokener(jsonSearchStr).nextValue() as JSONObject
            val jsonArray = jsonObject.getJSONArray("drinks")

            for (i in 0 until jsonArray.length()) {
                val drinkName = jsonArray.getJSONObject(i).getString("strDrink")
                val idDrink = jsonArray.getJSONObject(i).getString("idDrink")
                DRINKNAMES.add(DrinkObject(drinkName))
                DRINKID.add(idDrink)
                Log.i("STATUS_DRINKNAME", drinkName)
            }

            for (i in 0 until DRINKID.size) {
                drinkID = DRINKID[i]
                var objIDUrl = ""
                val idURL = Uri.Builder()
                    .scheme("https")
                    .authority("thecocktaildb.com")
                    .path("/api/json/v1/1/lookup.php")
                    .appendQueryParameter("i", drinkID)
                objIDUrl = idURL.build().toString()

                val searchUrl = URL(objIDUrl)
                val connection: HttpURLConnection = searchUrl.openConnection() as HttpURLConnection

                var jsonSearchStr = ""

                try {
                    jsonSearchStr = connection.getInputStream()
                        .bufferedReader().use(BufferedReader::readText)
                } finally {
                    connection.disconnect()
                }

                val drinkName = jsonArray.getJSONObject(i).getString("strDrink")
                val idDrink = jsonArray.getJSONObject(i).getString("idDrink")
                val strInstructions = jsonArray.getJSONObject(i).getString("strInstructions")
                val strDrinkThumb = jsonArray.getJSONObject(i).getString("strDrinkThumb")

                DRINKOBJ.add((DrinkObject("${drinkName}", "${idDrink}",
                    "${strInstructions}", "${strDrinkThumb}")))
                Log.i("STATUS_DRINKIDURL", objIDUrl)
            }

            withContext(Dispatchers.Main) {
                val adapter = MyAdapter()
                binding.drinkNameRecyclerView.setAdapter(adapter)
            }
        }
    }
}