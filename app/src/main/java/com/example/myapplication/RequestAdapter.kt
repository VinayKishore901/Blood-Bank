package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.request_layout.view.*


class RequestAdapter(
    private val dataSet: MutableList<RequestDataModel>, private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.request_layout, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        holder.itemView.message.text = dataSet.get(position).message
        Glide.with(context).load(dataSet[position].url).into(holder.itemView.image)
        holder.itemView.callbutton.setOnClickListener{}


        holder.itemView.sharebutton.setOnClickListener() {
        }
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var message: TextView
        lateinit var imageView: ImageView
        lateinit var callButton: ImageView
        lateinit var shareButton: ImageView
       init{

            message = itemView.findViewById(R.id.message)
            imageView = itemView.findViewById(R.id.image)
            callButton = itemView.findViewById(R.id.callbutton)
            shareButton = itemView.findViewById(R.id.sharebutton)

        }

    }




}