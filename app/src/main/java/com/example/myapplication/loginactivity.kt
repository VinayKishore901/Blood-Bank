package com.example.myapplication

import VolleySingleton
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest


class loginactivity : AppCompatActivity() {
lateinit var numberET : EditText
lateinit var passwordET :EditText
lateinit var submit_button : Button
lateinit var signupText: TextView
lateinit var tv : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginactivity)
        numberET = findViewById<EditText>(R.id.username)
        passwordET = findViewById<EditText>(R.id.password)
        submit_button = findViewById<Button>(R.id.submit_button)
        signupText =  findViewById<TextView>(R.id.sign_up_text)
        tv =findViewById<TextView>(R.id.tv)
        signupText.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this , registeractivity::class.java))

        })
        submit_button.setOnClickListener(View.OnClickListener {
                numberET.setError(null)
                passwordET.setError(null)
                var number :String = numberET.text.toString()
                var passworrd : String = passwordET.text.toString()
                if(isValid(number,passworrd))
                {
                    login(number,passworrd)
                }
        })
        tv.setOnClickListener(View.OnClickListener {

        })

    }
    private fun login(number : String ,password :String){
        val stringrequest : StringRequest = object : StringRequest(Request.Method.POST,
            login_url,Response.Listener<String>{ response->
            if(response == "success"){
                Toast.makeText(this,response , Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
                PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putString("number",number).apply()
                finish()
            }else{
                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
            }


        },Response.ErrorListener{error ->
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
            Log.d("Volley",error.message)

        })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): MutableMap<String, String> {
                val params : MutableMap<String,String> =HashMap()

                params["number"]=number
                params["password"]=password


                return params


            }
        }
        VolleySingleton.getInstance(this)!!.addToRequestQueue(stringrequest)

    }

    fun show(str : String)
    {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show()
    }

    private fun isValid(number :String , passworrd: String) : Boolean{
        if(number.isEmpty())
        {
            show("Number column is empty")
            numberET.setError("Empty number")
            return false
        }else if(passworrd.isEmpty()){
            show("password column is empty")
            numberET.setError("Empty password")
            return false
        }

        return true
    }

}
