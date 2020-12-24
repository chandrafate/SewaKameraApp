package com.candra.sewakameraapp.booking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.candra.sewakameraapp.R
import java.text.NumberFormat
import java.util.*

class ListBookingAdapter(private var data: List<Booking2>,
                         private val listener: (Booking2) -> Unit)
    : RecyclerView.Adapter<ListBookingAdapter.LeagueViewHolder>() {

    lateinit var ContextAdapter : Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListBookingAdapter.LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View = layoutInflater.inflate(R.layout.row_item_booking, parent, false)

        return ListBookingAdapter.LeagueViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ListBookingAdapter.LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)
    }

    override fun getItemCount(): Int = data.size

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivstatus: ImageView = view.findViewById(R.id.iv_status)
        private val tvtglin: TextView = view.findViewById(R.id.tv_tgl_in)
        private val tvtglout: TextView = view.findViewById(R.id.tv_tgl_out)
        private val tvharga: TextView = view.findViewById(R.id.tv_harga)
        private val tvjumlahitem: TextView = view.findViewById(R.id.tv_jumlah_item)

        fun bindItem(data: Booking2, listener: (Booking2) -> Unit, context : Context, position : Int) {

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

            var harga =  formatRupiah.format(data.total).toString()

            tvjumlahitem.text = data.jumlah_item.toString() + " Items"
            tvharga.text = harga.substring(0, harga.length - 3)
            tvtglin.text = data.tgl_in
            tvtglout.text = data.tgl_out

            if (data.status.equals("pending")) {
                ivstatus.setImageResource(R.drawable.ic_pending_yellow)
            } else if (data.status.equals("success")) {
                ivstatus.setImageResource(R.drawable.ic_success_green)
            }else if (data.status.equals("belum dikembalikan")) {
                ivstatus.setImageResource(R.drawable.ic_warning_red)
            }else if (data.status.equals("ditolak")) {
                ivstatus.setImageResource(R.drawable.ic_cancel_red)
            }


            itemView.setOnClickListener {
//                ngirim data ketika diclick
                listener(data)
            }
        }
    }

}
