package vcmsa.fake.projects.pixly.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import vcmsa.fake.projects.pixly.R

class PostAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]

        // Bind post data
        holder.captionTextView.text = post.caption
        holder.likeCountText.text = (post.likes ?: 0).toString()

        // Like button click
        holder.likeIcon.setOnClickListener {
            val postId = post.postId  // Ensure each Post has a postId field

            val postRef = firestore.collection("posts").document(postId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val newLikes = (snapshot.getLong("likes") ?: 0) + 1
                transaction.update(postRef, "likes", newLikes)
            }.addOnSuccessListener {
                holder.likeCountText.text = ((post.likes ?: 0) + 1).toString()
            }
        }
    }

    override fun getItemCount(): Int = postList.size

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val captionTextView: TextView = itemView.findViewById(R.id.captionTextView)
        val likeIcon: ImageView = itemView.findViewById(R.id.likeIcon)
        val likeCountText: TextView = itemView.findViewById(R.id.likeCountText)
    }
}

class Post {

}
