package com.resepku.app.model

data class Kategori(
    val id: Int,
    val nama: String
)

data class Resep(
    val id: Int,
    val kategoriId: Int,
    val judul: String,
    val deskripsi: String? = null,
    val gambar: String? = null,
    val porsiDasar: Int,
    val waktuMenit: Int,
    var mauDimasak: Boolean = false,
    val buatanPengguna: Boolean = false
)

data class BahanResep(
    val id: Int,
    val resepId: Int,
    val nama: String,
    val jumlah: Double? = null,
    val satuan: String? = null
)

data class Langkah(
    val id: Int,
    val resepId: Int,
    val urutan: Int,
    val instruksi: String
)
