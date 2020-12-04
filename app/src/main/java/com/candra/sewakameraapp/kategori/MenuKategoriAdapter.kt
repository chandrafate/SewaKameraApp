package com.candra.sewakameraapp.kategori

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.candra.sewakameraapp.R

class MenuKategoriAdapter(private var data: List<Kategori>,
                          private val listener: (Kategori) -> Unit)
    : RecyclerView.Adapter<MenuKategoriAdapter.LeagueViewHolder>() {

    lateinit var ContextAdapter : Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuKategoriAdapter.LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View = layoutInflater.inflate(R.layout.row_item_menu_kategori, parent, false)

        return LeagueViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: MenuKategoriAdapter.LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)
    }

    override fun getItemCount(): Int = data.size

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivkategori: ImageView = view.findViewById(R.id.iv_item)
        private val tvkategori: TextView = view.findViewById(R.id.tv_nama)

        fun bindItem(data: Kategori, listener: (Kategori) -> Unit, context : Context, position : Int) {

            tvkategori.text = data.nama

            Glide.with(context)
                .load(data.gambar)
                .override(320, 300)
                .into(ivkategori);

            itemView.setOnClickListener {
//                ngirim data ketika diclick
                listener(data)
            }
        }
    }

}
