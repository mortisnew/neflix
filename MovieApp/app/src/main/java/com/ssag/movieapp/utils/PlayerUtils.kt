package com.ssag.movieapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object PlayerUtils {
    fun openWithVLC(context: Context, url: String, title: String? = null) {
        try {
            val uri = Uri.parse(url)
            val vlcIntent = Intent(Intent.ACTION_VIEW)
            vlcIntent.setDataAndTypeAndNormalize(uri, "video/*")
            vlcIntent.`package` = "org.videolan.vlc"
            vlcIntent.putExtra("title", title)
            vlcIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(vlcIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "VLC is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWithKMPlayer(context: Context, url: String) {
        try {
            val uri = Uri.parse(url)
            val kmIntent = Intent(Intent.ACTION_VIEW)
            kmIntent.setDataAndTypeAndNormalize(uri, "video/*")
            kmIntent.`package` = "com.kmplayer"
            kmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(kmIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "KMPlayer is not installed", Toast.LENGTH_SHORT).show()
        }
    }
}
