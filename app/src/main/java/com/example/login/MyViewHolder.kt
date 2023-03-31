package com.example.login

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView


class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tvNombre: TextView
    var tvFecha: TextView
    var imageView: ImageView
    var cardView: CardView

    init {
        tvNombre = itemView.findViewById(R.id.tvNombre)
        tvFecha = itemView.findViewById(R.id.tvFecha)
        imageView = itemView.findViewById(R.id.imageView)
        cardView = itemView.findViewById(R.id.cardView)
    }
}