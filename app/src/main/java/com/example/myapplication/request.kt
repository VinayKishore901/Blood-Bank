package com.example.myapplication

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.preference.PreferenceManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.net.URISyntaxException


class request : AppCompatActivity() {
   lateinit var messageText: EditText
    lateinit var chooseImageText: TextView
    lateinit var postImage: ImageView
    lateinit var submit_button: Button
    var imageUri: Uri? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requestactivity)
        AndroidNetworking.initialize(applicationContext)
        messageText = findViewById(R.id.message)
        chooseImageText = findViewById(R.id.choose_text)
        postImage = findViewById(R.id.post_image)
        submit_button = findViewById(R.id.submit_button)
        submit_button.setOnClickListener(View.OnClickListener {
            if (isValid) {
                //code to upload this post.
                uploadRequest(messageText.getText().toString())
            }
        })
        chooseImageText.setOnClickListener(View.OnClickListener { //code to pick image
            permission()
        })
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 101)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun permission() {
        if (PermissionChecker.checkSelfPermission(
                applicationContext,
                permission.READ_EXTERNAL_STORAGE
            )
            != PermissionChecker.PERMISSION_GRANTED
        ) {
            //asking for permission
            requestPermissions(arrayOf(permission.READ_EXTERNAL_STORAGE), 401)
        } else {
            //permission is already there
            pickImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 401) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                //permission was granted
                pickImage()
            } else {
                //permission not granted
                showMessage("Permission Declined")
            }
        }
    }

    private fun uploadRequest(message: String) {
        //code to upload the message
        var path: String? = ""
        try {
            path = getPath(imageUri)
        } catch (e: URISyntaxException) {
            showMessage("wrong uri")
        }
        val number =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getString("number", "12345")
        AndroidNetworking.upload(upload_url)
            .addMultipartFile("file", File(path))
            .addQueryParameter("message", message)
            .addQueryParameter("number", number)
            .setPriority(Priority.HIGH)
            .build()
            .setUploadProgressListener { bytesUploaded, totalBytes -> // do anything with progress
                val progress = bytesUploaded / totalBytes * 100
                chooseImageText!!.text = "$progress%"
                chooseImageText!!.setOnClickListener(null)
            }
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        if (response.getBoolean("success")) {
                            showMessage("Succesfull")
                            finish()
                        } else {
                            showMessage(response.getString("message"))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError) {}
            })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.data
                Glide.with(applicationContext).load(imageUri).into(postImage!!)
            }
        }
    }

    private val isValid: Boolean
        private get() {
            if (messageText!!.text.toString().isEmpty()) {
                showMessage("Message shouldn't be empty")
                return false
            } else if (imageUri == null) {
                showMessage("Pick Image")
                return false
            }
            return true
        }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("NewApi")
    @Throws(URISyntaxException::class)
    private fun getPath(uri: Uri?): String? {
        var uri = uri
        val needToCheckUri = Build.VERSION.SDK_INT >= 19
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(applicationContext, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                return Environment.getExternalStorageDirectory()
                    .toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(
                    split[1]
                )
            }
        }
        if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            val projection = arrayOf(
                MediaStore.Images.Media.DATA
            )
            var cursor: Cursor? = null
            try {
                cursor = contentResolver
                    .query(uri, projection, selection, selectionArgs, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    companion object {
        fun isExternalStorageDocument(uri: Uri?): Boolean {
            return "com.android.externalstorage.documents" == uri!!.authority
        }

        fun isDownloadsDocument(uri: Uri?): Boolean {
            return "com.android.providers.downloads.documents" == uri!!.authority
        }

        fun isMediaDocument(uri: Uri?): Boolean {
            return "com.android.providers.media.documents" == uri!!.authority
        }
    }
}