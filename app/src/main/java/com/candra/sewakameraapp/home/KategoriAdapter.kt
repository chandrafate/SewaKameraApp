package com.candra.sewakameraapp.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.kategori.Kategori

class KategoriAdapter(private var data: List<Kategori>,
                      private val listener: (Kategori) -> Unit)
    : RecyclerView.Adapter<KategoriAdapter.LeagueViewHolder>() {

    lateinit var ContextAdapter : Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KategoriAdapter.LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View = layoutInflater.inflate(R.layout.row_item_kategori, parent, false)

        return LeagueViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: KategoriAdapter.LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)
    }

    //    limit yag di keluarkan
    override fun getItemCount(): Int = 3

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivkategori: ImageView = view.findViewById(R.id.iv_profile)
        private val tvkategori: TextView = view.findViewById(R.id.tv_jumlah_item)

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