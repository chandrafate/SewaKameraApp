package com.candra.sewakameraapp.adminlistbooking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.booking.Booking2
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AdminListBookingAdapter(private var data: List<Booking2>,
                              private val listener: (Booking2) -> Unit)
    : RecyclerView.Adapter<AdminListBookingAdapter.LeagueViewHolder>() {

    lateinit var ContextAdapter : Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminListBookingAdapter.LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View = layoutInflater.inflate(R.layout.row_item_booking_admin, parent, false)

        return AdminListBookingAdapter.LeagueViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: AdminListBookingAdapter.LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)
    }

    override fun getItemCount(): Int = data.size

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivstatus: ImageView = view.findViewById(R.id.iv_status)
        private val tvkode: TextView = view.findViewById(R.id.tv_kode_as)
        private val tvstatus: TextView = view.findViewById(R.id.tv_status)

        fun bindItem(data: Booking2, listener: (Booking2) -> Unit, context : Context, position : Int) {

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

            tvkode.text = data.key
            tvstatus.text = data.status

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
                tvstatus.text = "Belum Dikembalikan"
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
