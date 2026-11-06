package com.resepku.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.resepku.app.R
import com.resepku.app.data.ResepRepository
import com.resepku.app.databinding.ActivityDetailBinding
import com.resepku.app.databinding.ItemBahanBinding
import com.resepku.app.databinding.ItemLangkahBinding
import com.resepku.app.logic.HasilValidasi
import com.resepku.app.logic.LogikaResep
import com.resepku.app.model.Resep

class DetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(b.root)

        val resep = ResepRepository.resep(intent.getIntExtra(EXTRA_RESEP_ID, -1))
        if (resep == null) {
            Toast.makeText(this, R.string.err_resep_hilang, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        b.toolbar.tvJudulLayar.setText(R.string.judul_detail)
        b.toolbar.btnKembali.setOnClickListener { finish() }
        tampilkanInfo(resep)
        tampilkanBahan(resep)
        tampilkanLangkah(resep)
        pasangMauDimasak(resep)
        pasangHitungPorsi(resep)
    }

    private fun tampilkanInfo(r: Resep) {
        FotoResep.tampilkan(b.ivFotoResep, r)
        b.tvJudul.text = r.judul
        b.tvKategori.text = ResepRepository.namaKategori(r.kategoriId)
        b.tvLabelWaktu.pasangLabelWaktu(r.waktuMenit)
        b.tvNilaiWaktu.text = getString(R.string.format_menit, r.waktuMenit)
        b.tvNilaiPorsi.text = getString(R.string.format_porsi, r.porsiDasar)
        b.tvNilaiBahan.text = getString(R.string.format_bahan, ResepRepository.bahan(r.id).size)
        if (r.deskripsi.isNullOrBlank()) {
            b.cardTentang.visibility = View.GONE
        } else {
            b.tvDeskripsi.text = r.deskripsi
        }
        b.tvSubBahan.text = getString(R.string.takaran_untuk, r.porsiDasar)
    }

    private fun tampilkanBahan(r: Resep) {
        ResepRepository.bahan(r.id).forEach { bahan ->
            val row = ItemBahanBinding.inflate(layoutInflater, b.listBahan, true)
            row.tvNama.text = bahan.nama
            row.tvJumlah.text = teksJumlah(bahan)
        }
    }

    private fun tampilkanLangkah(r: Resep) {
        ResepRepository.langkah(r.id).forEach { langkah ->
            val row = ItemLangkahBinding.inflate(layoutInflater, b.listLangkah, true)
            row.tvNomor.text = langkah.urutan.toString()
            row.tvInstruksi.text = langkah.instruksi
        }
    }

    private fun pasangMauDimasak(r: Resep) {
        perbaruiMauDimasak(r)
        b.cardMauDimasak.setOnClickListener {
            ResepRepository.ubahMauDimasak(r)
            perbaruiMauDimasak(r)
        }
    }

    private fun perbaruiMauDimasak(r: Resep) {
        b.ivMauDimasak.setImageResource(if (r.mauDimasak) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border)
        b.tvStatusMauDimasak.setText(if (r.mauDimasak) R.string.sudah_ditandai else R.string.belum_ditandai)
    }

    private fun pasangHitungPorsi(r: Resep) {
        b.btnResetPorsi.text = getString(R.string.reset_porsi, r.porsiDasar)
        b.btnResetPorsi.setOnClickListener { isiPorsi(r.porsiDasar) }
        listOf<TextView>(b.chip2, b.chip6, b.chip10, b.chip15).zip(listOf(2, 6, 10, 15))
            .forEach { (chip, porsi) ->
                chip.text = getString(R.string.format_porsi, porsi)
                chip.setOnClickListener { isiPorsi(porsi) }
            }

        b.btnHitung.setOnClickListener {
            val hasil = LogikaResep.validasiBulat(b.etPorsi.text.toString(), LogikaResep.RENTANG_PORSI)
            if (hasil is HasilValidasi.Gagal) {
                b.etPorsi.error = pesanErrorPorsi(hasil.error)
                b.etPorsi.requestFocus()
                return@setOnClickListener
            }
            val porsiTarget = (hasil as HasilValidasi.Valid).nilai
            startActivity(
                Intent(this, HasilPorsiActivity::class.java)
                    .putExtra(HasilPorsiActivity.EXTRA_RESEP_ID, r.id)
                    .putExtra(HasilPorsiActivity.EXTRA_PORSI_TARGET, porsiTarget)
                    .putExtra(HasilPorsiActivity.EXTRA_FAKTOR, LogikaResep.hitungFaktor(porsiTarget, r.porsiDasar))
            )
        }
    }

    private fun isiPorsi(porsi: Int) {
        b.etPorsi.setText(porsi.toString())
        b.etPorsi.setSelection(b.etPorsi.text.length)
        b.etPorsi.error = null
    }

    companion object {
        const val EXTRA_RESEP_ID = "com.resepku.app.DETAIL_RESEP_ID"
    }
}
