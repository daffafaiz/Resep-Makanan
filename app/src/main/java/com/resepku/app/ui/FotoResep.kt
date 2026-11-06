package com.resepku.app.ui

import android.graphics.BitmapFactory
import android.widget.ImageView
import com.resepku.app.R
import com.resepku.app.model.Resep
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/** Foto contoh diambil dari Pexels; gambar dimuat di latar belakang agar daftar tetap lancar. */
object FotoResep {
    private val pemuat = Executors.newFixedThreadPool(3)
    private val cache = ConcurrentHashMap<String, android.graphics.Bitmap>()

    private val foto = mapOf(
        "Ayam Bakar Madu Gurih" to "14098784",
        "Nasi Goreng Telur Ceplok" to "8570300",
        "Bubur Ayam Kampung" to "36947004",
        "Sayur Asem Segar" to "1640777",
        "Soto Ayam Lamongan" to "12676932",
        "Tempe Orek Pedas Manis" to "37090664",
        "Pisang Goreng Renyah" to "37048319",
        "Bakwan Sayur" to "1640777",
        "Es Teh Lemon" to "34486580",
        "Wedang Jahe Hangat" to "5946974"
    )

    fun tampilkan(view: ImageView, resep: Resep) {
        val idFoto = foto[resep.judul] ?: "8570300"
        val url = "https://images.pexels.com/photos/$idFoto/pexels-photo-$idFoto.jpeg?auto=compress&cs=tinysrgb&w=1000"
        view.setTag(R.id.tag_url_foto, url)
        view.setImageDrawable(null)
        cache[url]?.let {
            view.setImageBitmap(it)
            return
        }
        pemuat.execute {
            val bitmap = runCatching {
                (URL(url).openConnection() as HttpURLConnection).run {
                    connectTimeout = 7000
                    readTimeout = 7000
                    doInput = true
                    try { inputStream.use(BitmapFactory::decodeStream) } finally { disconnect() }
                }
            }.getOrNull() ?: return@execute
            cache[url] = bitmap
            view.post {
                if (view.getTag(R.id.tag_url_foto) == url) view.setImageBitmap(bitmap)
            }
        }
    }
}
