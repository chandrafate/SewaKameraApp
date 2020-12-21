package com.candra.sewakameraapp.checkout

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.candra.sewakameraapp.R
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
    }

    private fun pushBooking(booking: Booking) {
        var key = mDatabase.push().getKey()

        booking.key = key

        mDatabase.child(key!!).setValue(booking)

        idKeranjang.forEach {
            var keyBarang = mDatabase.child(key!!).child("barang").push().getKey()
            mDatabase.child(key).child("barang").child(keyBarang!!).child("id").setValue(it.id)
            mDatabase.child(key).child("barang").child(keyBarang!!).child("key").setValue(keyBarang!!)
        }

        deleteKeranjang(booking.username!!)

        showNotif(booking)
        startActivity(Intent(this, SuccessCheckoutActivity::class.java))
    }

    private fun deleteKeranjang(username: String) {
        val mydatabase = FirebaseDatabase.getInstance().getReference("member")
        mydatabase.child(username).child("keranjang").removeValue()
    }


    private fun showNotif(booking: Booking) {
        val NOTIFICATION_CHANNEL_ID = "channel_bwa_notif"
        val context = this.applicationContext
        var notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelName = "BWAMOV Notif Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        val mIntent = Intent(this, SuccessCheckoutActivity::class.java)
        val bundle = Bundle()
//        bundle.putParcelable("data", datas)
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
            .setTicker("notif bwa starting")
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setLights(Color.RED, 3000, 3000)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setContentTitle("Sukses Terbeli")
            .setContentText("Booking sudah berhasil!")

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(115, builder.build())
    }
}