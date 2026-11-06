package com.resepku.app.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.resepku.app.R
import com.resepku.app.data.ResepRepository
import com.resepku.app.databinding.ActivityHasilPorsiBinding
import com.resepku.app.databinding.ItemBahanHasilBinding
import com.resepku.app.logic.LogikaResep
import com.resepku.app.model.BahanResep

class HasilPorsiActivity : AppCompatActivity() {
    private lateinit var b: ActivityHasilPorsiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHasilPorsiBinding.inflate(layoutInflater)
        setContentView(b.root)

        val resep = ResepRepository.resep(intent.getIntExtra(EXTRA_RESEP_ID, -1))
        val porsiTarget = intent.getIntExtra(EXTRA_PORSI_TARGET, -1)
        val faktor = intent.getDoubleExtra(EXTRA_FAKTOR, -1.0)
        if (resep == null || porsiTarget < 1 || faktor <= 0) {
            Toast.makeText(this, R.string.err_resep_hilang, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        b.toolbar.tvJudulLayar.setText(R.string.judul_hasil)
        b.toolbar.btnKembali.setOnClickListener { finish() }
        b.btnUbahPorsi.setOnClickListener { finish() }

        val teksFaktor = LogikaResep.formatAngka(faktor)
        b.tvJudul.text = resep.judul
        b.tvWaktu.text = getString(R.string.format_menit, resep.waktuMenit)
        b.tvLabelWaktu.pasangLabelWaktu(resep.waktuMenit)
        b.tvPorsiAwal.text = resep.porsiDasar.toString()
        b.tvPorsiTarget.text = porsiTarget.toString()
        b.tvFaktor.text = getString(R.string.format_faktor, teksFaktor)
        b.tvAturan.text = getString(R.string.aturan_waktu, resep.waktuMenit)

        val bahan = ResepRepository.bahan(resep.id)
        b.tvJumlahKomponen.text = getString(R.string.format_bahan, bahan.size)
        bahan.forEach { tambahBaris(it, faktor, teksFaktor) }
    }

    private fun tambahBaris(bahan: BahanResep, faktor: Double, teksFaktor: String) {
        val row = ItemBahanHasilBinding.inflate(layoutInflater, b.listBahan, true)
        row.tvNama.text = bahan.nama
        val jumlahBaru = LogikaResep.skalakan(bahan.jumlah, faktor)
        row.tvJumlah.text = teksJumlah(jumlahBaru, bahan.satuan)
        if (bahan.jumlah == null || jumlahBaru == null) {
            row.tvBasis.setText(R.string.sesuai_selera)
            row.tvSelisih.setText(R.string.konstan)
            row.tvSelisih.setBackgroundResource(R.drawable.bg_pill_netral)
            return
        }
        row.tvBasis.text = getString(R.string.format_basis, teksJumlah(bahan), teksFaktor)
        val selisih = jumlahBaru - bahan.jumlah
        when {
            selisih > 0 -> row.tvSelisih.text = getString(R.string.format_tambah, teksJumlah(selisih, bahan.satuan))
            selisih < 0 -> row.tvSelisih.text = getString(R.string.format_kurang, teksJumlah(-selisih, bahan.satuan))
            else -> row.tvSelisih.visibility = View.GONE
        }
    }

    companion object {
        const val EXTRA_RESEP_ID = "com.resepku.app.EXTRA_RESEP_ID"
        const val EXTRA_PORSI_TARGET = "com.resepku.app.EXTRA_PORSI_TARGET"
        const val EXTRA_FAKTOR = "com.resepku.app.EXTRA_FAKTOR"
    }
}
