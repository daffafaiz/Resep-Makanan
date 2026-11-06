package com.resepku.app.ui

import android.content.Context
import android.content.res.ColorStateList
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.resepku.app.R
import com.resepku.app.logic.ErrorAngka
import com.resepku.app.logic.LabelWaktu
import com.resepku.app.logic.LogikaResep
import com.resepku.app.model.BahanResep

@StringRes
fun LabelWaktu.teksRes(): Int = when (this) {
    LabelWaktu.KILAT -> R.string.label_kilat
    LabelWaktu.CEPAT -> R.string.label_cepat
    LabelWaktu.SEDANG -> R.string.label_sedang
    LabelWaktu.LAMA -> R.string.label_lama
}

/** Memasang teks dan warna label waktu pada pill. */
fun TextView.pasangLabelWaktu(waktuMenit: Int) {
    val label = LogikaResep.labelWaktu(waktuMenit)
    val (latar, teks) = when (label) {
        LabelWaktu.KILAT -> R.color.secondary to R.color.white
        LabelWaktu.CEPAT -> R.color.secondary_container to R.color.on_secondary_container
        LabelWaktu.SEDANG -> R.color.tertiary to R.color.white
        LabelWaktu.LAMA -> R.color.primary_fixed to R.color.primary
    }
    setText(label.teksRes())
    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, latar))
    setTextColor(ContextCompat.getColor(context, teks))
}

/** Contoh hasil: "300 g", atau "Secukupnya" bila jumlah kosong. */
fun Context.teksJumlah(jumlah: Double?, satuan: String?): String =
    if (jumlah == null) getString(R.string.secukupnya)
    else listOfNotNull(LogikaResep.formatAngka(jumlah), satuan).joinToString(" ")

fun Context.teksJumlah(bahan: BahanResep): String = teksJumlah(bahan.jumlah, bahan.satuan)

fun Context.pesanErrorPorsi(error: ErrorAngka): String = when (error) {
    ErrorAngka.KOSONG -> getString(R.string.err_porsi_kosong)
    ErrorAngka.BUKAN_ANGKA -> getString(R.string.err_porsi_angka)
    ErrorAngka.DILUAR_RENTANG -> getString(R.string.err_porsi_rentang)
}

fun Context.pesanErrorWaktu(error: ErrorAngka): String = when (error) {
    ErrorAngka.KOSONG -> getString(R.string.err_waktu_kosong)
    ErrorAngka.BUKAN_ANGKA -> getString(R.string.err_waktu_angka)
    ErrorAngka.DILUAR_RENTANG -> getString(R.string.err_waktu_rentang)
}
