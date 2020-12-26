package com.candra.sewakameraapp.checkout

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.booking.Booking2
import com.candra.sewakameraapp.booking.DetailBookingActivity
import com.candra.sewakameraapp.keranjang.Keranjang
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : AppCompatActivity() {

    private var idKeranjang = ArrayList<Keranjang>()

    lateinit var mDatabase: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        mDatabase = FirebaseDatabase.getInstance().getReference("booking")

        idKeranjang = intent.getSerializableExtra("keranjang") as ArrayList<Keranjang>

        val booking = intent.getParcelableExtra<Booking>("booking")


        iv_back.setOnClickListener {
            finish()
        }

        btn_checkout.setOnClickListener {
            pushBooking(booking!!)
        }

        iv_ovo.setOnClickListener {
            iv_ovo.setImageResource(R.drawable.ovo_selected)
            iv_dana.setImageResource(R.drawable.dana_unselected)
            iv_cod.setImageResource(R.drawable.cod_unselected)
            tv_ket.text = "1. Silahkan transfer sesuai total booking anda \\n2. Harap mengirim foto / screenshot bukti pembayaran untuk mempercepat proses verifikasi\\n3. Transfer sesuai nomor tujuan OVO di bawah ini :"
            tv_norek.text = "081 2345 6789"
            tv_norek.visibility = View.VISIBLE
            tv_copy.visibility = View.VISIBLE
        }

        iv_dana.setOnClickListener {
            iv_ovo.setImageResource(R.drawable.ovo_unselected)
            iv_dana.setImageResource(R.drawable.dana_selected)
            iv_cod.setImageResource(R.drawable.cod_unselected)
            tv_ket.text = "1. Silahkan transfer sesuai total booking anda \n2. Harap mengirim foto / screenshot bukti pembayaran untuk mempercepat proses verifikasi\n3. Transfer sesuai nomor tujuan DANA di bawah ini :"
            tv_norek.text = "089 8765 4321"
            tv_norek.visibility = View.VISIBLE
            tv_copy.visibility = View.VISIBLE
        }
        iv_cod.setOnClickListener {
            iv_ovo.setImageResource(R.drawable.ovo_unselected)
            iv_dana.setImageResource(R.drawable.dana_unselected)
            iv_cod.setImageResource(R.drawable.cod_selected)
            tv_ket.text = "1. Pembayaran langsung di toko\n2. Kekurangan tidak dapat dipastikan stok barang tersedia"
            tv_norek.visibility = View.INVISIBLE
            tv_copy.visibility = View.INVISIBLE
        }
    }

    private fun pushBooking(booking: Booking) {
        var key = (10000..99999).random().toString()

        booking.key = key

        mDatabase.child(key!!).setValue(booking)

        idKeranjang.forEach {
            var keyBarang = mDatabase.child(key!!).child("barang").push().getKey()
            mDatabase.child(key).child("barang").child(keyBarang!!).child("id").setValue(it.id)
            mDatabase.child(key).child("barang").child(keyBarang!!).child("key").setValue(keyBarang!!)
        }

        deleteKeranjang(booking.username!!)

//        warisan success
        val booking2 = Booking2()
        booking2.key = key
        booking2.denda = booking.denda
        booking2.status = booking.status
        booking2.tgl_in = booking.tgl_in
        booking2.tgl_out = booking.tgl_out
        booking2.total = booking.total
        booking2.username = booking.username
        booking2.jumlah_item = idKeranjang.size.toLong()


        showNotif(booking2)
        startActivity(Intent(this, SuccessCheckoutActivity::class.java).putExtra("detailBooking", booking2))
    }

    private fun deleteKeranjang(username: String) {
        val mydatabase = FirebaseDatabase.getInstance().getReference("member")
        mydatabase.child(username).child("keranjang").removeValue()
    }


    private fun showNotif(booking2: Booking2) {
        val NOTIFICATION_CHANNEL_ID = "channel_sewa_notif"
        val context = this.applicationContext
        var notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelName = "SewaKam Notif Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        val mIntent = Intent(this, DetailBookingActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("detailBooking", booking2)
        mIntent.putExtras(bundle)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        builder.setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notif)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.ic_notif
                )
            )
            .setTicker("notif sewa starting")
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setLights(Color.RED, 3000, 3000)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setContentTitle("Sukses Checkout")
            .setContentText("Checkout dengan kode ${booking2.key} sudah berhasil!")

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(115, builder.build())
    }
}