package com.resepku.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.chip.Chip
import com.resepku.app.R
import com.resepku.app.data.ResepRepository
import com.resepku.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private var kategoriId: Int? = null
    private var hanyaMauDimasak = false
    private var idChipSemua = View.NO_ID

    private val adapter = ResepAdapter(
        onKlik = { r ->
            startActivity(
                Intent(this, DetailActivity::class.java)
                    .putExtra(DetailActivity.EXTRA_RESEP_ID, r.id)
            )
        },
        onTandai = { r ->
            ResepRepository.ubahMauDimasak(r)
            muatUlang()
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.toolbar.btnKembali.visibility = View.GONE
        b.toolbar.tvJudulLayar.setText(R.string.judul_daftar)
        b.rvResep.adapter = adapter

        b.etCari.addTextChangedListener { muatUlang() }
        b.tabSemua.setOnClickListener { hanyaMauDimasak = false; muatUlang() }
        b.tabMauDimasak.setOnClickListener { hanyaMauDimasak = true; muatUlang() }
        b.fabTambah.setOnClickListener {
            startActivity(Intent(this, TambahResepActivity::class.java))
        }
        b.btnResetFilter.setOnClickListener {
            b.etCari.text.clear()
            hanyaMauDimasak = false
            kategoriId = null
            b.chipKategori.check(idChipSemua)
            muatUlang()
        }
        pasangChipKategori()
    }

    override fun onResume() {
        super.onResume()
        muatUlang()
    }

    private fun pasangChipKategori() {
        val pilihan = listOf<Pair<Int?, String>>(null to getString(R.string.semua)) +
            ResepRepository.kategori.map { it.id to it.nama }
        pilihan.forEach { (id, nama) ->
            val chip = layoutInflater.inflate(R.layout.view_chip, b.chipKategori, false) as Chip
            chip.id = View.generateViewId()
            chip.text = nama
            chip.setOnClickListener { kategoriId = id; muatUlang() }
            b.chipKategori.addView(chip)
            if (id == null) idChipSemua = chip.id
        }
        b.chipKategori.check(idChipSemua)
    }

    private fun muatUlang() {
        val hasil = ResepRepository.cari(b.etCari.text.toString(), kategoriId, hanyaMauDimasak)
        adapter.kirim(hasil)
        val kosong = hasil.isEmpty()
        b.rvResep.visibility = if (kosong) View.GONE else View.VISIBLE
        b.layoutKosong.visibility = if (kosong) View.VISIBLE else View.GONE

        b.tabSemua.text = getString(R.string.tab_semua, ResepRepository.jumlahSemua())
        b.tabMauDimasak.text = getString(R.string.tab_mau_dimasak, ResepRepository.jumlahMauDimasak())
        gayaTab(b.tabSemua, !hanyaMauDimasak)
        gayaTab(b.tabMauDimasak, hanyaMauDimasak)
    }

    private fun gayaTab(tab: TextView, aktif: Boolean) {
        tab.setBackgroundResource(if (aktif) R.drawable.bg_tab_aktif else 0)
        tab.setTextColor(ContextCompat.getColor(this, if (aktif) R.color.primary else R.color.on_surface_variant))
    }
}
