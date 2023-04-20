package com.example.networkapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    // file name
    private val internalFilename = "my_file"

    // pointer to file in storage
    private lateinit var file: File

    //*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }
        if(true){
            //loadComic()
        }

        if (intent?.action == Intent.ACTION_VIEW){
            intent.data?.path?.run{
                //Log.d("Comic number", split("/")[1])
                downloadComic(split("/")[1])

            }
        }

        findViewById<Button>(R.id.button).setOnClickListener(){
            try {
                val intent = Intent(
                    Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                    Uri.parse("package:${packageName}")
                )
                startActivity(intent)
            } catch (e:Exception){

            }
        }

        // Create file reference(to internal storage) for app-specific file
        file = File(filesDir, internalFilename)

        // Load string data and pass in String data as JSONObject into showComic function
        if (file.exists()) {
            try {
                val br = BufferedReader(FileReader(file))
                val text = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    text.append(line)
                    text.append('\n')
                }
                br.close()
                showComic(JSONObject(text.toString()))

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    //*
    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url, {showComic(it)
                try {
                    // save the JSONObject that represents the comic into the file we created
                    val outputStream = FileOutputStream(file)
                    outputStream.write(it.toString().toByteArray())
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                                   }

                , {

            })

        )
    }

    private fun showComic (comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    override fun onStop(){
        super.onStop()

        //file.delete()

    }


}