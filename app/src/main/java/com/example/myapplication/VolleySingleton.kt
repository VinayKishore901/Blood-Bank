import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(private val mContext: Context) {
    private var mRequestQueue: RequestQueue?
    // If RequestQueue is null the initialize new RequestQueue

    // Return RequestQueue
    val requestQueue: RequestQueue?
        get() {
            // If RequestQueue is null the initialize new RequestQueue
            if (mRequestQueue == null) {
                mRequestQueue =
                    Volley.newRequestQueue(mContext.applicationContext)
            }

            // Return RequestQueue
            return mRequestQueue
        }

    fun <T> addToRequestQueue(request: Request<T>?) {
        // Add the specified request to the request queue
        requestQueue!!.add(request)
    }

    companion object {
        private var mInstance: VolleySingleton? = null

        @Synchronized
        fun getInstance(context: Context): VolleySingleton? {
            // If Instance is null then initialize new Instance
            if (mInstance == null) {
                mInstance = VolleySingleton(context)
            }
            // Return MySingleton new Instance
            return mInstance
        }
    }

    init {
        // Specify the application context
        // Get the request queue
        mRequestQueue = requestQueue
    }
}