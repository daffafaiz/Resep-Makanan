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
        "Ayam Bakar Madu Gurih" to "https://i.pinimg.com/736x/3e/97/4d/3e974d9b34bb395bbd9ac7af50a9ec41.jpg",
        "Nasi Goreng Telur Ceplok" to "https://tse3.mm.bing.net/th/id/OIP.gHIkiggHli3gfsnFsrNydAHaEo?r=0&rs=1&pid=ImgDetMain&o=7&rm=3",
        "Bubur Ayam Kmpung" to "https://i.pinimg.com/736x/84/fc/b0/84fcb0f18a713f2c9d3e8407ffcc7a01.jpg",
        "Sayur Asem Segar" to "https://i.pinimg.com/736x/8e/15/9c/8e159cdab08cc37586bb23cc92b7f3a3.jpg",
        "Soto Ayam Lamongan" to "https://i.pinimg.com/736x/03/1d/0c/031d0ce8b03e9d7427c60e76e79584ac.jpg",
        "Tempe Orek Pedas Manis" to "https://i.pinimg.com/1200x/65/77/ee/6577ee8805a9ce980ecbcf614bb83617.jpg",
        "Pisang Goreng Renyah" to "https://i.pinimg.com/736x/a9/ee/b1/a9eeb17125f76fe28397bdb5073a9026.jpg",
        "Bakwan Sayur" to "https://i.pinimg.com/736x/7f/e2/b5/7fe2b599a836b5e1e58c10f43584627d.jpg",
        "Es Teh Lemon" to "https://i.pinimg.com/736x/e0/20/09/e02009f4ddd985f6c59f67939938f8c9.jpg",
        "Wedang Jahe Hangat" to "https://i.pinimg.com/1200x/b2/cc/a4/b2cca40e8000e779e70d5f4f9c49f9b7.jpg",
    )

    fun tampilkan(view: ImageView, resep: Resep) {
        val url = foto[resep.judul] ?: return
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
