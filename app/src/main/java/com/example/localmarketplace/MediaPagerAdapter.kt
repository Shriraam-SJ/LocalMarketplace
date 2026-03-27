package com.example.localmarketplace

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

class MediaPagerAdapter(private val mediaItems: List<MediaItem>) :
    RecyclerView.Adapter<MediaPagerAdapter.MediaViewHolder>() {

    class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.ivPagerImage)
        val videoView: VideoView = view.findViewById(R.id.vvPagerVideo)
        val btnPlay: ImageButton = view.findViewById(R.id.btnPlayVideo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media_pager, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val item = mediaItems[position]
        if (item.isImage) {
            holder.imageView.visibility = View.VISIBLE
            holder.videoView.visibility = View.GONE
            holder.btnPlay.visibility = View.GONE
            
            val imageBytes = Base64.decode(item.base64, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            holder.imageView.setImageBitmap(decodedImage)
        } else {
            holder.imageView.visibility = View.GONE
            holder.videoView.visibility = View.VISIBLE
            holder.btnPlay.visibility = View.VISIBLE
            
            val videoFile = File(holder.itemView.context.cacheDir, "temp_video_${position}.mp4")
            if (!videoFile.exists()) {
                val videoBytes = Base64.decode(item.base64, Base64.DEFAULT)
                FileOutputStream(videoFile).use { it.write(videoBytes) }
            }
            
            holder.videoView.setVideoURI(Uri.fromFile(videoFile))
            
            holder.btnPlay.setOnClickListener {
                holder.videoView.start()
                holder.btnPlay.visibility = View.GONE
            }
            
            holder.videoView.setOnCompletionListener {
                holder.btnPlay.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = mediaItems.size
}

data class MediaItem(val base64: String, val isImage: Boolean)
