package com.example.myapplication

import VolleySingleton
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    lateinit var recyclerview : RecyclerView
    lateinit var Adapter : RequestAdapter
    private var RequestDataModels: MutableList<RequestDataModel> = ArrayList()
    lateinit var btn : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn = findViewById<TextView>(R.id.make_request_button)

        btn.setOnClickListener {
            startActivity(Intent(this , request::class.java))

        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.search_button) {
                //open search
                startActivity(Intent(this, SearchActivity::class.java))
            }
            true
        }


        recyclerview = findViewById(R.id.recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL , false)
        Adapter  = RequestAdapter(RequestDataModels, this)
        recyclerview.setAdapter(Adapter)
        populateHomePage()



    }
    private fun populateHomePage() {
        val city =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getString("city", "no_city")
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            get_url,
            Response.Listener { response ->
                val gson = Gson()
                val type = object :
                    TypeToken<List<RequestDataModel?>?>() {}.type
                val dataModels =
                    gson.fromJson<List<RequestDataModel>>(response, type)
                RequestDataModels!!.addAll(dataModels)
                Adapter!!.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@MainActivity, "Something went wrong:(", Toast.LENGTH_SHORT)
                    .show()
                Log.d(
                    "VOLLEY",
                    Objects.requireNonNull(error.message)
                )
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()

                return params
            }
        }
        VolleySingleton.getInstance(this)!!.addToRequestQueue(stringRequest)
    }
}
