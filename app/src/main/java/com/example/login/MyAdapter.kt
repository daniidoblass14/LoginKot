package com.example.login

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class MyAdapter(private val mDataList: List<MyData>) :
    RecyclerView.Adapter<MyViewHolder>() {
    private var onClick: onItemClick? = null
    private var onLongClick: onItemLongClick? = null

    interface onItemClick {
        fun onItemClick(data: MyData?)
    }

    interface onItemLongClick {
        fun onItemLongClick(data: MyData?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = mDataList[position]
        holder.tvNombre.setText(data.nombre)
        holder.tvFecha.setText(data.fecha)
        holder.imageView.setImageBitmap(data.imageBitmap)

        // Cambiar el color de fondo dependiendo de la posición
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.RED)
        } else {
            holder.itemView.setBackgroundColor(Color.BLUE)
        }
        holder.cardView.setOnClickListener(View.OnClickListener {
            onClick?.onItemClick(data)
        })

        holder.cardView.setOnLongClickListener(OnLongClickListener {
            onLongClick?.onItemLongClick(data)
            false
        })
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    fun setOnClick(onClick: onItemClick?) {
        this.onClick = onClick
    }

    fun setOnLongClick(onLongClick: onItemLongClick?) {
        this.onLongClick = onLongClick
    }
}