package com.candra.sewakameraapp.adminBarang

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

class AdminKategoriAdapter(private var data: List<Kategori>,
                           private val listener: (Kategori) -> Unit)
    : RecyclerView.Adapter<AdminKategoriAdapter.LeagueViewHolder>() {

    lateinit var ContextAdapter : Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminKategoriAdapter.LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View = layoutInflater.inflate(R.layout.row_item_kategori_admin, parent, false)

        return LeagueViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: AdminKategoriAdapter.LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)
    }

    override fun getItemCount(): Int = data.size

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivkategori: ImageView = view.findViewById(R.id.iv_gambar_item_kategori_admin)
        private val tvkategori: TextView = view.findViewById(R.id.tv_nama_item_kategori_admin)
        private val tvjumlah: TextView = view.findViewById(R.id.tv_jumlah_item_kategori_admin)

        fun bindItem(data: Kategori, listener: (Kategori) -> Unit, context : Context, position : Int) {

            tvkategori.text = data.nama
            tvjumlah.text = "${data.item} Items"

            Glide.with(context)
                .load(data.gambar)
                .override(300, 300)
                .into(ivkategori);

            itemView.setOnClickListener {
//                ngirim data ketika diclick
                listener(data)
            }
        }
    }

}
