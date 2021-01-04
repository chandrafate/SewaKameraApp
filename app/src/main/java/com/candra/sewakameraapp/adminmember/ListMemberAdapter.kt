package com.candra.sewakameraapp.adminmember

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.sign.Member

class ListMemberAdapter(private var data: List<Member>,
                        private val listener: (Member) -> Unit)
    : RecyclerView.Adapter<ListMemberAdapter.LeagueViewHolder>() {

    lateinit var ContextAdapter : Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListMemberAdapter.LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View = layoutInflater.inflate(R.layout.row_member_admin, parent, false)

        return ListMemberAdapter.LeagueViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ListMemberAdapter.LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)
    }

    override fun getItemCount(): Int = data.size

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivpic: ImageView = view.findViewById(R.id.iv_profile)
        private val tvnama: TextView = view.findViewById(R.id.tv_nama)
        private val tvusername: TextView = view.findViewById(R.id.tv_username)

        fun bindItem(data: Member, listener: (Member) -> Unit, context : Context, position : Int) {

            tvnama.text = data.nama
            tvusername.text = data.username

            Glide.with(context)
                .load(data.gambar)
                .placeholder(R.drawable.user_pic)
                .apply(RequestOptions.circleCropTransform())
                .into(ivpic);

            itemView.setOnClickListener {
//                ngirim data ketika diclick
                listener(data)
            }
        }
    }
}