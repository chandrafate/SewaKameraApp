package com.candra.sewakameraapp.adminpromo

import android.content.Context
import android.graphics.Paint
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

class ListPromoAdminAdapter(private var barang: List<Barang>,private var promo: List<Promo>, private val listener2: (Promo) -> Unit) : RecyclerView.Adapter<ListPromoAdminAdapter.LeagueViewHolder>() {

    lateinit var ContextAdapter : Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View = layoutInflater.inflate(R.layout.row_item_promo_admin, parent, false)

        return LeagueViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        holder.bindItem(barang[position], promo[position], listener2, ContextAdapter, position)
    }

    override fun getItemCount(): Int = barang.size

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivitem: ImageView = view.findViewById(R.id.iv_image_item_promo_admin)
        private val tvnama: TextView = view.findViewById(R.id.tv_nama_item_promo_admin)
        private val tvharga: TextView = view.findViewById(R.id.tv_harga_item_promo_admin)
        private val tvhargapromo: TextView = view.findViewById(R.id.tv_hargapromo_item_promo_admin)
        private val tvtgl: TextView = view.findViewById(R.id.tv_harga_item_tanggal_admin)

        fun bindItem(data: Barang, data2: Promo, listener: (Promo) -> Unit, context : Context, position : Int) {

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

            tvnama.text = data.nama
            tvharga.text = formatRupiah.format(data.harga).toString()
            tvtgl.text = data2.expired

            tvhargapromo.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                text = formatRupiah.format(data2.harga).toString()
            }

            Glide.with(context)
                .load(data.gambar)
                .into(ivitem);

            itemView.setOnClickListener {
//                ngirim data ketika diclick
                listener(data2)
            }
        }
    }

}
