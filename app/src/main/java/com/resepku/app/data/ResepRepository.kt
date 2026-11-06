package com.resepku.app.data

import com.resepku.app.model.BahanResep
import com.resepku.app.model.Kategori
import com.resepku.app.model.Langkah
import com.resepku.app.model.Resep

/** Bahan baru sebelum diberi id: nama, jumlah (null = secukupnya), satuan. */
typealias BahanBaru = Triple<String, Double?, String?>

/** Penyimpanan di memori. Perubahan hilang saat proses aplikasi berakhir (PRD bagian 8). */
object ResepRepository {
    private const val SARAPAN = 1
    private const val UTAMA = 2
    private const val CAMILAN = 3
    private const val MINUMAN = 4

    val kategori = listOf(
        Kategori(SARAPAN, "Sarapan"),
        Kategori(UTAMA, "Makanan Utama"),
        Kategori(CAMILAN, "Camilan"),
        Kategori(MINUMAN, "Minuman")
    )

    private val daftarResep = mutableListOf<Resep>()
    private val daftarBahan = mutableListOf<BahanResep>()
    private val daftarLangkah = mutableListOf<Langkah>()
    private var idBerikutnya = 1

    init {
        seed(UTAMA, "Ayam Bakar Madu Gurih", "Ayam bakar berbalut bumbu madu dan bawang putih, cocok untuk santap siang maupun malam.", 4, 30,
            listOf(b("Dada ayam", 200.0, "g"), b("Madu murni", 3.0, "sdm"), b("Kecap manis", 2.0, "sdm"), b("Bawang putih", 4.0, "siung"), b("Garam & lada")),
            listOf("Haluskan bawang putih, campur dengan madu, kecap, garam, dan lada. Lumuri ayam, diamkan 15 menit.", "Panaskan wajan pemanggang dengan sedikit minyak di atas api sedang.", "Panggang ayam sambil diolesi sisa bumbu hingga matang merata, sekitar 12–15 menit.", "Angkat, diamkan 2 menit, lalu sajikan dengan nasi hangat."))
        seed(SARAPAN, "Nasi Goreng Telur Ceplok", "Nasi goreng sederhana dengan telur ceplok untuk sarapan cepat.", 2, 15,
            listOf(b("Nasi putih", 400.0, "g"), b("Telur", 2.0, "butir"), b("Bawang merah", 4.0, "siung"), b("Kecap manis", 2.0, "sdm"), b("Garam")),
            listOf("Iris bawang merah, lalu tumis hingga harum.", "Masukkan nasi, kecap, dan garam. Aduk rata di atas api besar.", "Goreng telur ceplok secara terpisah.", "Sajikan nasi dengan telur di atasnya."))
        seed(SARAPAN, "Bubur Ayam Kampung", "Bubur lembut dengan suwiran ayam dan kuah gurih.", 4, 40,
            listOf(b("Beras", 200.0, "g"), b("Air", 1500.0, "ml"), b("Dada ayam", 250.0, "g"), b("Daun salam", 2.0, "lembar"), b("Garam")),
            listOf("Rebus ayam hingga matang, lalu suwir dagingnya.", "Masak beras dengan air kaldu dan daun salam sambil diaduk.", "Setelah menjadi bubur, tambahkan garam.", "Sajikan dengan suwiran ayam."))
        seed(UTAMA, "Sayur Asem Segar", "Sayur berkuah asam segar khas Sunda.", 5, 25,
            listOf(b("Labu siam", 1.0, "buah"), b("Kacang panjang", 100.0, "g"), b("Jagung manis", 1.0, "buah"), b("Asam jawa", 2.0, "sdm"), b("Air", 1500.0, "ml"), b("Garam & gula")),
            listOf("Potong semua sayuran.", "Didihkan air bersama asam jawa.", "Masukkan jagung dan labu, masak hingga empuk.", "Tambahkan kacang panjang, garam, dan gula, lalu masak sebentar."))
        seed(UTAMA, "Soto Ayam Lamongan", "Soto kuah kuning dengan taburan koya yang gurih.", 6, 75,
            listOf(b("Ayam", 800.0, "g"), b("Air", 2000.0, "ml"), b("Bawang putih", 6.0, "siung"), b("Kunyit", 2.0, "ruas"), b("Serai", 2.0, "batang"), b("Koya")),
            listOf("Rebus ayam dengan serai hingga empuk, lalu suwir dagingnya.", "Haluskan bawang putih dan kunyit, lalu tumis hingga harum.", "Masukkan bumbu ke kaldu, masak 20 menit.", "Sajikan kuah dengan suwiran ayam dan koya."))
        seed(UTAMA, "Tempe Orek Pedas Manis", "Lauk kering tempe yang tahan lama dan mudah dibuat.", 4, 20,
            listOf(b("Tempe", 300.0, "g"), b("Cabai merah", 3.0, "buah"), b("Bawang merah", 4.0, "siung"), b("Kecap manis", 3.0, "sdm"), b("Garam")),
            listOf("Potong tipis tempe, lalu goreng hingga kering.", "Tumis bawang dan cabai hingga harum.", "Masukkan tempe, kecap, dan garam. Aduk hingga meresap."))
        seed(CAMILAN, "Pisang Goreng Renyah", "Pisang goreng berbalut tepung yang renyah.", 4, 20,
            listOf(b("Pisang kepok", 6.0, "buah"), b("Tepung terigu", 150.0, "g"), b("Air", 150.0, "ml"), b("Garam"), b("Minyak goreng")),
            listOf("Campur tepung, air, dan garam hingga rata.", "Celupkan pisang ke dalam adonan.", "Goreng hingga kuning keemasan, lalu tiriskan."))
        seed(CAMILAN, "Bakwan Sayur", "Gorengan sayur yang cocok sebagai camilan sore.", 4, 30,
            listOf(b("Kol", 100.0, "g"), b("Wortel", 1.0, "buah"), b("Tepung terigu", 200.0, "g"), b("Air", 180.0, "ml"), b("Garam & merica")),
            listOf("Iris tipis kol dan wortel.", "Campur sayuran dengan tepung, air, garam, dan merica.", "Goreng sesendok demi sesendok hingga kecokelatan."))
        seed(MINUMAN, "Es Teh Lemon", "Teh dingin dengan perasan lemon yang menyegarkan.", 2, 5,
            listOf(b("Teh celup", 2.0, "kantong"), b("Air panas", 400.0, "ml"), b("Air lemon", 2.0, "sdm"), b("Gula", 2.0, "sdm"), b("Es batu")),
            listOf("Seduh teh dengan air panas, lalu larutkan gula.", "Tambahkan air lemon dan aduk.", "Tuang ke gelas berisi es batu."))
        seed(MINUMAN, "Wedang Jahe Hangat", "Minuman jahe hangat untuk menghangatkan badan.", 2, 15,
            listOf(b("Jahe", 50.0, "g"), b("Air", 500.0, "ml"), b("Gula merah", 3.0, "sdm"), b("Serai", 1.0, "batang")),
            listOf("Memarkan jahe dan serai.", "Rebus bersama air dan gula merah selama 10 menit.", "Saring, lalu sajikan selagi hangat."))
    }

    private fun b(nama: String, jumlah: Double? = null, satuan: String? = null): BahanBaru =
        Triple(nama, jumlah, satuan)

    private fun seed(
        kategoriId: Int, judul: String, deskripsi: String, porsi: Int, waktu: Int,
        bahan: List<BahanBaru>, langkah: List<String>
    ) {
        tambahResep(kategoriId, judul, deskripsi, porsi, waktu, bahan, langkah, false)
    }

    fun tambahResep(
        kategoriId: Int, judul: String, deskripsi: String?, porsiDasar: Int, waktuMenit: Int,
        bahan: List<BahanBaru>, langkah: List<String>, buatanPengguna: Boolean = true
    ): Resep {
        val resep = Resep(
            id = idBerikutnya++, kategoriId = kategoriId, judul = judul, deskripsi = deskripsi,
            porsiDasar = porsiDasar, waktuMenit = waktuMenit, buatanPengguna = buatanPengguna
        )
        daftarResep.add(resep)
        bahan.forEach { (nama, jumlah, satuan) ->
            daftarBahan.add(BahanResep(idBerikutnya++, resep.id, nama, jumlah, satuan))
        }
        langkah.forEachIndexed { i, teks ->
            daftarLangkah.add(Langkah(idBerikutnya++, resep.id, i + 1, teks))
        }
        return resep
    }

    fun resep(id: Int): Resep? = daftarResep.find { it.id == id }

    fun namaKategori(id: Int): String = kategori.first { it.id == id }.nama

    fun bahan(resepId: Int): List<BahanResep> = daftarBahan.filter { it.resepId == resepId }

    fun langkah(resepId: Int): List<Langkah> =
        daftarLangkah.filter { it.resepId == resepId }.sortedBy { it.urutan }

    fun jumlahSemua(): Int = daftarResep.size

    fun jumlahMauDimasak(): Int = daftarResep.count { it.mauDimasak }

    fun ubahMauDimasak(resep: Resep) {
        resep.mauDimasak = !resep.mauDimasak
    }

    /** Resep buatan pengguna tampil paling atas. */
    fun cari(kataKunci: String, kategoriId: Int?, hanyaMauDimasak: Boolean): List<Resep> {
        val kunci = kataKunci.trim()
        return daftarResep.filter { r ->
            (kategoriId == null || r.kategoriId == kategoriId) &&
                (!hanyaMauDimasak || r.mauDimasak) &&
                (kunci.isEmpty() || r.judul.contains(kunci, true) ||
                    bahan(r.id).any { it.nama.contains(kunci, true) })
        }.sortedByDescending { it.buatanPengguna }
    }
}
