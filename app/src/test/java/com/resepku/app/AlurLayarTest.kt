package com.resepku.app

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.resepku.app.data.ResepRepository
import com.resepku.app.ui.DetailActivity
import com.resepku.app.ui.HasilPorsiActivity
import com.resepku.app.ui.MainActivity
import com.resepku.app.ui.TambahResepActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Menjalankan alur layar sesuai PRD bagian 9 tanpa emulator. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w411dp-h1400dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AlurLayarTest {
    private val ayamBakar get() = ResepRepository.cari("Ayam Bakar", null, false).first()

    private fun detail(): DetailActivity = Robolectric.buildActivity(
        DetailActivity::class.java,
        Intent(ApplicationProvider.getApplicationContext(), DetailActivity::class.java)
            .putExtra(DetailActivity.EXTRA_RESEP_ID, ayamBakar.id)
    ).setup().get()

    private fun Activity.et(id: Int) = findViewById<EditText>(id)

    private fun hitung(a: DetailActivity, teks: String): Intent? {
        a.et(R.id.etPorsi).setText(teks)
        a.findViewById<View>(R.id.btnHitung).performClick()
        return shadowOf(a).nextStartedActivity
    }

    private fun simpanGambar(a: Activity, nama: String) {
        val folder = System.getProperty("layar.dir") ?: return
        val root = a.window.decorView
        root.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(2400, View.MeasureSpec.EXACTLY)
        )
        root.layout(0, 0, 1080, 2400)
        val bmp = Bitmap.createBitmap(1080, 2400, Bitmap.Config.ARGB_8888)
        root.draw(Canvas(bmp))
        File(folder).mkdirs()
        File(folder, "$nama.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test fun daftar_menampilkanMinimal10Resep() {
        val a = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val rv = a.findViewById<RecyclerView>(R.id.rvResep)
        assertTrue(rv.adapter!!.itemCount >= 10)
        simpanGambar(a, "1_daftar")
    }

    @Test fun cari_berdasarkanBahan_danKosong() {
        val a = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val rv = a.findViewById<RecyclerView>(R.id.rvResep)
        a.et(R.id.etCari).setText("tempe")
        assertEquals(1, rv.adapter!!.itemCount)
        a.et(R.id.etCari).setText("zzz")
        assertEquals(View.VISIBLE, a.findViewById<View>(R.id.layoutKosong).visibility)
        a.findViewById<View>(R.id.btnResetFilter).performClick()
        assertTrue(rv.adapter!!.itemCount >= 10)
    }

    @Test fun u01_u08_porsiValid_pindahDanHasilBenar() {
        val a = detail()
        simpanGambar(a, "2_detail")
        val intent = hitung(a, "6")
        assertNotNull(intent)
        assertEquals(1.5, intent!!.getDoubleExtra(HasilPorsiActivity.EXTRA_FAKTOR, -1.0), 0.0)

        val h = Robolectric.buildActivity(HasilPorsiActivity::class.java, intent).setup().get()
        val list = h.findViewById<LinearLayout>(R.id.listBahan)
        fun jumlah(i: Int) = list.getChildAt(i).findViewById<TextView>(R.id.tvJumlah).text.toString()
        assertEquals("300 g", jumlah(0))
        assertEquals("4.5 sdm", jumlah(1))
        assertEquals("Secukupnya", jumlah(4))
        assertEquals("1.5x", h.findViewById<TextView>(R.id.tvFaktor).text.toString())
        simpanGambar(h, "3_hasil")
    }

    @Test fun u03_u07_porsiTidakValid_tetapDiLayar() {
        val a = detail()
        val harapan = mapOf(
            "" to R.string.err_porsi_kosong,
            "dua" to R.string.err_porsi_angka,
            "0" to R.string.err_porsi_rentang,
            "51" to R.string.err_porsi_rentang,
            "99999999999" to R.string.err_porsi_angka
        )
        harapan.forEach { (input, pesan) ->
            assertNull(input, hitung(a, input))
            assertEquals(a.getString(pesan), a.et(R.id.etPorsi).error.toString())
        }
    }

    @Test fun u11_tandaiMauDimasak_munculDiDaftar() {
        val a = detail()
        a.findViewById<View>(R.id.cardMauDimasak).performClick()
        assertEquals(1, ResepRepository.cari("", null, true).count { it.id == ayamBakar.id })
        val m = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        m.findViewById<View>(R.id.tabMauDimasak).performClick()
        assertTrue(m.findViewById<RecyclerView>(R.id.rvResep).adapter!!.itemCount >= 1)
        a.findViewById<View>(R.id.cardMauDimasak).performClick()
    }

    @Test fun u10_tambahResep_validasiLaluSimpan() {
        val a = Robolectric.buildActivity(TambahResepActivity::class.java).setup().get()
        val simpan = a.findViewById<View>(R.id.btnSimpan)
        simpanGambar(a, "4_tambah")
        val awal = ResepRepository.jumlahSemua()

        a.et(R.id.etJudul).setText("     ")
        simpan.performClick()
        assertEquals(a.getString(R.string.err_judul), a.et(R.id.etJudul).error.toString())

        a.et(R.id.etJudul).setText("Telur Dadar")
        simpan.performClick()
        assertEquals(View.VISIBLE, a.findViewById<View>(R.id.tvErrorKategori).visibility)

        a.findViewById<View>(R.id.katSarapan).performClick()
        simpan.performClick()
        assertEquals(a.getString(R.string.err_waktu_kosong), a.et(R.id.etWaktu).error.toString())

        a.et(R.id.etWaktu).setText("10")
        simpan.performClick()
        assertEquals(a.getString(R.string.err_bahan_kosong), a.et(R.id.etNama).error.toString())

        a.et(R.id.etNama).setText("Telur")
        a.et(R.id.etJumlah).setText("Infinity")
        simpan.performClick()
        assertEquals(a.getString(R.string.err_bahan_jumlah), a.et(R.id.etJumlah).error.toString())

        a.et(R.id.etJumlah).setText("2")
        simpan.performClick()
        assertEquals(a.getString(R.string.err_langkah_kosong), a.et(R.id.etInstruksi).error.toString())
        assertEquals(awal, ResepRepository.jumlahSemua())
        assertFalse(a.isFinishing)

        a.et(R.id.etInstruksi).setText("Kocok dan goreng telur.")
        simpan.performClick()
        assertEquals(awal + 1, ResepRepository.jumlahSemua())
        assertTrue(a.isFinishing)
        assertEquals("Telur Dadar", ResepRepository.cari("", null, false).first().judul)
    }
}
