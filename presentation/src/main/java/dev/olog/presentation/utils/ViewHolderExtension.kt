package dev.olog.presentation.utils

import android.support.v7.widget.RecyclerView

fun <T> RecyclerView.ViewHolder.setOnClickListener(data: List<T>, func: (item: T) -> Unit){
    itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition])
        }
    }
}

fun <T> RecyclerView.ViewHolder.setOnLongClickListener(data: List<T>, func: (item: T) -> Unit){
    itemView.setOnLongClickListener inner@ {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition])
            return@inner true
        }
        false
    }
}