package com.example.firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostAdapter(private val postList: List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val birthPlaceTextView: TextView = view.findViewById(R.id.birthPlaceTextView)
        val birthDateTextView: TextView = view.findViewById(R.id.birthDateTextView)
        val deathDateTextView: TextView = view.findViewById(R.id.deathDateTextView)
        val contributionsTextView: TextView = view.findViewById(R.id.contributionsTextView)
        val postImageView: ImageView = view.findViewById(R.id.postImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.emailTextView.text = post.email
        holder.nameTextView.text = "${post.name} ${post.surname}"
        holder.birthPlaceTextView.text = "Doğum Yeri: ${post.birthPlace}"
        holder.birthDateTextView.text = "Doğum Tarihi: ${post.birthDate}"
        holder.deathDateTextView.text = "Ölüm Tarihi: ${post.deathDate}"
        holder.contributionsTextView.text = "Katkıları: ${post.contributions}"

        // Eğer imageUrl varsa resmi yükler, yoksa varsayılan bir görünüm gösterir.
        if (post.imageUr.isNotEmpty()) {
            Glide.with(holder.itemView.context).load(post.imageUr).into(holder.postImageView)
        } else {
            holder.postImageView.setImageResource(android.R.color.transparent)
        }
    }

    override fun getItemCount() = postList.size
}
