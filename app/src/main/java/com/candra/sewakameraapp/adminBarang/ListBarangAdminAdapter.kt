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
import com.candra.sewakameraapp.barang.Barang
import java.text.NumberFormat
import java.util.*

class ListBarangAdminAdapter(private var data: List<Barang>,
                             private val listener: (Barang) -> Unit)
    : RecyclerView.Adapter<ListBarangAdminAdapter.LeagueViewHolder>() {

    lateinit var ContextAdapter : Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View = layoutInflater.inflate(R.layout.row_item_produk_admin, parent, false)

        return LeagueViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)
    }

    override fun getItemCount(): Int = data.size

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivitem: ImageView = view.findViewById(R.id.iv_profile)
        private val tvnama: TextView = view.findViewById(R.id.tv_jumlah_item)
        private val tvharga: TextView = view.findViewById(R.id.tv_harga)

        fun bindItem(data: Barang, listener: (Barang) -> Unit, context : Context, position : Int) {

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

            tvnama.text = data.nama
            tvharga.text = formatRupiah.format(data.harga).toString()

            Glide.with(context)
                .load(data.gambar)
                .override(250, 250)
                .into(ivitem);

            itemView.setOnClickListener {
//                ngirim data ketika diclick
                listener(data)
            }
        }
    }

}
