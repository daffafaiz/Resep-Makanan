package com.resepku.app.logic

import java.math.BigDecimal
import java.math.RoundingMode

enum class LabelWaktu { KILAT, CEPAT, SEDANG, LAMA }

enum class ErrorAngka { KOSONG, BUKAN_ANGKA, DILUAR_RENTANG }

sealed class HasilValidasi {
    data class Valid(val nilai: Int) : HasilValidasi()
    data class Gagal(val error: ErrorAngka) : HasilValidasi()
}

/** Logika murni tanpa ketergantungan Android agar mudah diuji. */
object LogikaResep {
    val RENTANG_PORSI = 1..50
    val RENTANG_WAKTU = 1..1440
    const val PANJANG_JUDUL_MIN = 3
    private const val JUMLAH_MAKS = 100_000.0

    fun validasiBulat(teks: String, rentang: IntRange): HasilValidasi {
        val bersih = teks.trim()
        if (bersih.isEmpty()) return HasilValidasi.Gagal(ErrorAngka.KOSONG)
        val angka = bersih.toIntOrNull() ?: return HasilValidasi.Gagal(ErrorAngka.BUKAN_ANGKA)
        if (angka !in rentang) return HasilValidasi.Gagal(ErrorAngka.DILUAR_RENTANG)
        return HasilValidasi.Valid(angka)
    }

    fun judulValid(teks: String): Boolean = teks.trim().length >= PANJANG_JUDUL_MIN

    /** Mengembalikan null bila teks bukan jumlah yang sah. Koma diterima sebagai desimal. */
    fun parseJumlah(teks: String): Double? {
        val angka = teks.trim().replace(',', '.').toDoubleOrNull() ?: return null
        // toDoubleOrNull juga menerima "NaN" dan "Infinity", jadi rentang wajib diperiksa.
        return angka.takeIf { it > 0 && it <= JUMLAH_MAKS }
    }

    fun hitungFaktor(porsiTarget: Int, porsiDasar: Int): Double =
        porsiTarget.toDouble() / porsiDasar

    fun skalakan(jumlah: Double?, faktor: Double): Double? = jumlah?.let { it * faktor }

    fun labelWaktu(waktuMenit: Int): LabelWaktu = when (waktuMenit) {
        in 1..15 -> LabelWaktu.KILAT
        in 16..30 -> LabelWaktu.CEPAT
        in 31..60 -> LabelWaktu.SEDANG
        else -> LabelWaktu.LAMA
    }

    /** Maksimal 2 desimal, angka bulat tanpa desimal: 300.0 menjadi "300". */
    fun formatAngka(nilai: Double): String {
        val bulat = BigDecimal(nilai).setScale(2, RoundingMode.HALF_UP)
        if (bulat.signum() == 0) return if (nilai == 0.0) "0" else "< 0.01"
        return bulat.stripTrailingZeros().toPlainString()
    }
}
