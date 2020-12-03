package com.example.myapplication

import VolleySingleton
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest as StringRequest1



class registeractivity : AppCompatActivity() {
    lateinit var nameET: EditText

    lateinit var cityET: EditText
    lateinit var passwordET: EditText
    lateinit var numberET: EditText
    lateinit var bloodgroupET: EditText
    lateinit var submit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeractivity)

        nameET = findViewById<EditText>(R.id.name)
        cityET = findViewById<EditText>(R.id.city)
        passwordET = findViewById<EditText>(R.id.password)
        numberET = findViewById<EditText>(R.id.number)
        bloodgroupET = findViewById<EditText>(R.id.bloodgroup)
        submit = findViewById<Button>(R.id.submitbutton)
        submit.setOnClickListener(View.OnClickListener {
            var name: String = nameET.text.toString()
            var city: String = cityET.text.toString()
            var password: String = passwordET.text.toString()
            var number: String = numberET.text.toString()
            var bloodgroup: String = bloodgroupET.text.toString()
            show(name + "\n" + city + "\n" + password + "\n" + number + "\n" + bloodgroup)
            if(isValid(name, city, password, number, bloodgroup)) {
                register(name, city, bloodgroup, password, number)
            }

        })
    }
    private fun register(name:String, city:String, blood_group: String, password: String, number: String){
    val stringRequest: StringRequest1 = object : StringRequest1(
        Method.POST, register_url, Response.Listener { response ->
            if (response == "success") {

                Toast.makeText(this,response, Toast.LENGTH_SHORT).show()
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
                finish()

            } else {
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            }
        },
        Response.ErrorListener { error ->
            Toast.makeText(
                this, "Something went wrong:(", Toast.LENGTH_SHORT).show()
            Log.d("VOLLEY", error.message)
        }) {
        @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params: MutableMap<String, String> =
                HashMap()

            params["name"] = name
            params["city"] = city
            params["blood_groups"] = blood_group
            params["password"] = password
            params["number"] = number
            return params
        }
    }
    VolleySingleton.getInstance(this)!!.addToRequestQueue(stringRequest)
}

fun isValid(
    name: String,
    city: String,
    password: String,
    number: String,
    bloodgroup: String
): Boolean {
    var valid_list: ArrayList<String> = ArrayList()
    valid_list.add("A+")
    valid_list.add("A-")
    valid_list.add("AB+")
    valid_list.add("AB-")
    valid_list.add("O+")
    valid_list.add("O-")
    valid_list.add("B+")
    valid_list.add("B-")

    if (name.isEmpty()) {
        show("Name is empty")
        return false;
    } else if (city.isEmpty()) {
        show("City is empty")
        return false;
    } else if (!valid_list.contains(bloodgroup)) {
        show("Invalid Blood Group$bloodgroup")
        return false
    } else if (number.length != 10) {
        show("invalid mobile number ,must be of 10 digits")
        return false
    }



    return true;

}

fun show(str: String) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}
}
