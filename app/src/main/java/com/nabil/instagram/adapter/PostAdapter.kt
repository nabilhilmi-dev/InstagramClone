package com.nabil.instagram.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nabil.instagram.CommentActivity
import com.nabil.instagram.MainActivity
import com.nabil.instagram.R
import com.nabil.instagram.model.Post
import com.nabil.instagram.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.post_layout.view.*

class PostAdapter(private val mContext: Context, private val mPost: List<Post>):RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = null

    class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {
        var profileimage: CircleImageView = itemView.findViewById(R.id.user_profile_image_post)
        var imagepost: ImageView = itemView.findViewById(R.id.post_image_home)
        var likebutton: ImageView = itemView.findViewById(R.id.post_image_like_post_btn)
        var commentbutton: ImageView = itemView.findViewById(R.id.img_post_comment_btn)
        var savebutton:  ImageView = itemView.findViewById(R.id.img_save_post_btn)
        var username: TextView = itemView.findViewById(R.id.post_user_name)
        var likes: TextView = itemView.findViewById(R.id.post_likes)
        var publisher: TextView = itemView.findViewById(R.id.post_publisher)
        var description: TextView = itemView.findViewById(R.id.post_desc)
        var comments: TextView = itemView.findViewById(R.id.post_comment)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]
        Picasso.get().load(post.getPostimage()).into(holder.imagepost)
        if (post.getDescription().equals("")){
            holder.description.visibility = View.GONE
        }else{
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.getDescription())
        }

        publisherInfo(holder.profileimage, holder.username, holder.publisher, post.getPublisher())
        //method setting icon
        isLikes(post.getPostid(), holder.likebutton)
        //method untuk melihat beberapa user yang liat
        numberOfLikes(holder.likes,post.getPostid())
        getTotalComment(holder.comments, post.getPostid())

        holder.likebutton.setOnClickListener {
            if (holder.likebutton.tag == "Demen"){
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(post.getPostid()).child(firebaseUser!!.uid)
                    .setValue(true)
            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(post.getPostid()).child(firebaseUser!!.uid)
                    .removeValue()

                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }

        holder.commentbutton.setOnClickListener {
            val intentComment = Intent(mContext, CommentActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.comments.setOnClickListener {
            val intentComment = Intent(mContext, CommentActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }
        
    }

    private fun getTotalComment(comments: TextView, postid: String) {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.exists()){
                   comments.text = "view all" + p0.childrenCount.toString() + "comments"
               }
            }
        })
    }

    private fun numberOfLikes(likes: TextView, postid: String) {
        val likeRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        likeRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.exists()){
                   likes.text = p0.childrenCount.toString() + "likes"
               }
            }
        })

    }

    private fun isLikes(postid: String, likebutton: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        likesRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {


            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){
                    likebutton.setImageResource(R.drawable.heart_clicked)
                    likebutton.tag = "Didemenin"

                }
                else
                {
                    likebutton.setImageResource(R.drawable.heart_not_clicked)
                    likebutton.tag = "Demen"


                }
            }
        })

    }

    private fun publisherInfo(
        profileimage: CircleImageView,
        username: TextView,
        publisher: TextView,
        publisherID: String
    ) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                   val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile)
                        .into(profileimage)
                    username.text = user?.getUsername()
                    publisher.text = user?.getFullname()
                }
            }
        })
    }
}