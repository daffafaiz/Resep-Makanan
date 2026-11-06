package com.resepku.app.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.resepku.app.R
import com.resepku.app.data.BahanBaru
import com.resepku.app.data.ResepRepository
import com.resepku.app.databinding.ActivityTambahResepBinding
import com.resepku.app.databinding.ItemInputBahanBinding
import com.resepku.app.databinding.ItemInputLangkahBinding
import com.resepku.app.logic.HasilValidasi
import com.resepku.app.logic.LogikaResep

class TambahResepActivity : AppCompatActivity() {
    private lateinit var b: ActivityTambahResepBinding
    private val barisBahan = mutableListOf<ItemInputBahanBinding>()
    private val barisLangkah = mutableListOf<ItemInputLangkahBinding>()
    private var kategoriId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTambahResepBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.toolbar.tvJudulLayar.setText(R.string.judul_tambah)
        b.toolbar.btnKembali.setOnClickListener { finish() }
        b.btnBatal.setOnClickListener { finish() }

        pasangPenghitung()
        pasangKategori()
        tambahBarisBahan()
        repeat(2) { tambahBarisLangkah() }
        b.btnTambahBahan.setOnClickListener { tambahBarisBahan() }
        b.btnTambahLangkah.setOnClickListener { tambahBarisLangkah() }
        b.btnSimpan.setOnClickListener { simpan() }
    }

    private fun pasangPenghitung() {
        b.tvJumlahKarakter.text = getString(R.string.format_karakter, 0)
        b.etJudul.addTextChangedListener {
            b.tvJumlahKarakter.text = getString(R.string.format_karakter, it?.trim()?.length ?: 0)
        }
        b.btnKurangPorsi.setOnClickListener { ubahPorsi(-1) }
        b.btnTambahPorsi.setOnClickListener { ubahPorsi(1) }
        b.etWaktu.addTextChangedListener {
            val hasil = LogikaResep.validasiBulat(it.toString(), LogikaResep.RENTANG_WAKTU)
            if (hasil is HasilValidasi.Valid) {
                b.tvLabelWaktu.pasangLabelWaktu(hasil.nilai)
                b.tvLabelWaktu.visibility = View.VISIBLE
            } else {
                b.tvLabelWaktu.visibility = View.GONE
            }
        }
    }

    private fun ubahPorsi(selisih: Int) {
        val r = LogikaResep.RENTANG_PORSI
        val baru = ((b.etPorsi.text.toString().trim().toIntOrNull() ?: PORSI_AWAL) + selisih)
            .coerceIn(r.first, r.last)
        b.etPorsi.setText(baru.toString())
        b.etPorsi.error = null
    }

    private fun pasangKategori() {
        val kartu = listOf(b.katSarapan, b.katUtama, b.katCamilan, b.katMinuman)
        ResepRepository.kategori.zip(kartu).forEach { (kategori, view) ->
            view.text = kategori.nama
            view.setOnClickListener {
                kategoriId = kategori.id
                b.tvErrorKategori.visibility = View.GONE
                kartu.forEach { gayaKategori(it, it === view) }
            }
            gayaKategori(view, false)
        }
    }

    private fun gayaKategori(view: TextView, aktif: Boolean) {
        view.setBackgroundResource(if (aktif) R.drawable.bg_pilihan_aktif else R.drawable.bg_pilihan)
        view.setTextColor(ContextCompat.getColor(this, if (aktif) R.color.primary else R.color.on_surface))
    }

    private fun tambahBarisBahan() {
        val row = ItemInputBahanBinding.inflate(layoutInflater, b.listInputBahan, true)
        val adapter = ArrayAdapter.createFromResource(this, R.array.satuan, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        row.spSatuan.adapter = adapter
        row.btnHapus.setOnClickListener {
            b.listInputBahan.removeView(row.root)
            barisBahan.remove(row)
            perbaruiBaris()
        }
        barisBahan.add(row)
        perbaruiBaris()
    }

    private fun tambahBarisLangkah() {
        val row = ItemInputLangkahBinding.inflate(layoutInflater, b.listInputLangkah, true)
        row.btnHapus.setOnClickListener {
            b.listInputLangkah.removeView(row.root)
            barisLangkah.remove(row)
            perbaruiBaris()
        }
        barisLangkah.add(row)
        perbaruiBaris()
    }

    private fun perbaruiBaris() {
        barisBahan.forEachIndexed { i, row ->
            row.tvNomor.text = (i + 1).toString()
            row.btnHapus.visibility = if (barisBahan.size > 1) View.VISIBLE else View.INVISIBLE
        }
        barisLangkah.forEachIndexed { i, row ->
            row.tvNomor.text = (i + 1).toString()
            row.btnHapus.visibility = if (barisLangkah.size > 1) View.VISIBLE else View.INVISIBLE
        }
        b.tvJumlahBaris.text = getString(R.string.format_bahan, barisBahan.size)
        b.tvJumlahLangkah.text = getString(R.string.format_langkah, barisLangkah.size)
    }

    /** Periksa berurutan sesuai PRD 7.1: berhenti pada kesalahan pertama. */
    private fun simpan() {
        val judul = b.etJudul.text.toString().trim()
        if (!LogikaResep.judulValid(judul)) {
            b.etJudul.error = getString(R.string.err_judul)
            b.etJudul.requestFocus()
            return
        }

        val kategori = kategoriId
        if (kategori == null) {
            b.tvErrorKategori.visibility = View.VISIBLE
            b.scroll.smoothScrollTo(0, b.cardKategori.top)
            return
        }

        val porsi = LogikaResep.validasiBulat(b.etPorsi.text.toString(), LogikaResep.RENTANG_PORSI)
        if (porsi is HasilValidasi.Gagal) {
            b.etPorsi.error = pesanErrorPorsi(porsi.error)
            b.etPorsi.requestFocus()
            return
        }

        val waktu = LogikaResep.validasiBulat(b.etWaktu.text.toString(), LogikaResep.RENTANG_WAKTU)
        if (waktu is HasilValidasi.Gagal) {
            b.etWaktu.error = pesanErrorWaktu(waktu.error)
            b.etWaktu.requestFocus()
            return
        }

        val bahan = mutableListOf<BahanBaru>()
        for (row in barisBahan) {
            val nama = row.etNama.text.toString().trim()
            val teksJumlah = row.etJumlah.text.toString().trim()
            if (nama.isEmpty()) {
                if (teksJumlah.isEmpty()) continue
                row.etNama.error = getString(R.string.err_bahan_nama)
                row.etNama.requestFocus()
                return
            }
            val satuan = row.spSatuan.selectedItem.toString()
            if (teksJumlah.isEmpty() || satuan == getString(R.string.satuan_secukupnya)) {
                bahan.add(Triple(nama, null, null))
                continue
            }
            val jumlah = LogikaResep.parseJumlah(teksJumlah)
            if (jumlah == null) {
                row.etJumlah.error = getString(R.string.err_bahan_jumlah)
                row.etJumlah.requestFocus()
                return
            }
            bahan.add(Triple(nama, jumlah, satuan))
        }
        if (bahan.isEmpty()) {
            val pertama = barisBahan.first().etNama
            pertama.error = getString(R.string.err_bahan_kosong)
            pertama.requestFocus()
            return
        }

        val langkah = barisLangkah.map { it.etInstruksi.text.toString().trim() }.filter { it.isNotEmpty() }
        if (langkah.isEmpty()) {
            val pertama = barisLangkah.first().etInstruksi
            pertama.error = getString(R.string.err_langkah_kosong)
            pertama.requestFocus()
            return
        }

        ResepRepository.tambahResep(
            kategoriId = kategori,
            judul = judul,
            deskripsi = null,
            porsiDasar = (porsi as HasilValidasi.Valid).nilai,
            waktuMenit = (waktu as HasilValidasi.Valid).nilai,
            bahan = bahan,
            langkah = langkah
        )
        Toast.makeText(this, R.string.resep_tersimpan, Toast.LENGTH_SHORT).show()
        finish()
    }

    private companion object {
        const val PORSI_AWAL = 4
    }
}
