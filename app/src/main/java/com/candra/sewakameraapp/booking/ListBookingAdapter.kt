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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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

        private val ivstatus: ImageView = view.findViewById(R.id.iv_status_item_booking)
        private val tvtglin: TextView = view.findViewById(R.id.tv_tgl_in_item_booking)
        private val tvtglout: TextView = view.findViewById(R.id.tv_tgl_out_item_booking)
        private val tvharga: TextView = view.findViewById(R.id.tv_harga_item_booking)
        private val tvjumlahitem: TextView = view.findViewById(R.id.tv_jumlah_item_booking)

        fun bindItem(data: Booking2, listener: (Booking2) -> Unit, context : Context, position : Int) {

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

            var harga =  formatRupiah.format(data.total).toString()

            tvjumlahitem.text = data.jumlah_item.toString() + " Items"
            tvharga.text = harga.substring(0, harga.length - 3)
            tvtglin.text = data.tgl_in
            tvtglout.text = data.tgl_out

//            cek telat
            val skrang = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val format = SimpleDateFormat("dd/MM/yyyy")
            val days = TimeUnit.DAYS.convert(
                format.parse(skrang).getTime() -
                        format.parse(data.tgl_out).getTime(),
                TimeUnit.MILLISECONDS
            )

            if (data.status.equals("pending")) {
                ivstatus.setImageResource(R.drawable.ic_pending_yellow)
            } else if (data.status.equals("success") && days > 0) {
                ivstatus.setImageResource(R.drawable.ic_warning_red)
            }else if (data.status.equals("success")) {
                ivstatus.setImageResource(R.drawable.ic_success_green)
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