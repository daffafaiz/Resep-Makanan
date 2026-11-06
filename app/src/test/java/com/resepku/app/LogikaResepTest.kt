package com.resepku.app

import com.resepku.app.logic.ErrorAngka
import com.resepku.app.logic.HasilValidasi
import com.resepku.app.logic.LabelWaktu
import com.resepku.app.logic.LogikaResep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class LogikaResepTest {
    private val porsi = LogikaResep.RENTANG_PORSI

    private fun gagal(e: ErrorAngka) = HasilValidasi.Gagal(e)

    @Test fun u01_faktorDanBahan() {
        val f = LogikaResep.hitungFaktor(6, 4)
        assertEquals(1.5, f, 0.0)
        assertEquals(300.0, LogikaResep.skalakan(200.0, f)!!, 0.0)
    }

    @Test fun u02_porsiSama() = assertEquals(1.0, LogikaResep.hitungFaktor(4, 4), 0.0)

    @Test fun u03_kosong() {
        assertEquals(gagal(ErrorAngka.KOSONG), LogikaResep.validasiBulat("", porsi))
        assertEquals(gagal(ErrorAngka.KOSONG), LogikaResep.validasiBulat("   ", porsi))
    }

    @Test fun u04_u07_bukanAngka() {
        assertEquals(gagal(ErrorAngka.BUKAN_ANGKA), LogikaResep.validasiBulat("dua", porsi))
        assertEquals(gagal(ErrorAngka.BUKAN_ANGKA), LogikaResep.validasiBulat("99999999999", porsi))
    }

    @Test fun u05_u06_rentang() {
        assertEquals(gagal(ErrorAngka.DILUAR_RENTANG), LogikaResep.validasiBulat("0", porsi))
        assertEquals(gagal(ErrorAngka.DILUAR_RENTANG), LogikaResep.validasiBulat("51", porsi))
        assertEquals(HasilValidasi.Valid(50), LogikaResep.validasiBulat("50", porsi))
    }

    @Test fun u08_secukupnyaTetapNull() = assertNull(LogikaResep.skalakan(null, 1.5))

    @Test fun u09_labelWaktu() {
        val hasil = listOf(15, 16, 30, 31, 60, 61).map(LogikaResep::labelWaktu)
        assertEquals(
            listOf(LabelWaktu.KILAT, LabelWaktu.CEPAT, LabelWaktu.CEPAT,
                LabelWaktu.SEDANG, LabelWaktu.SEDANG, LabelWaktu.LAMA),
            hasil
        )
    }

    @Test fun u10_judulSpasi() = assertFalse(LogikaResep.judulValid("     "))

    @Test fun formatAngka() {
        assertEquals("300", LogikaResep.formatAngka(300.0))
        assertEquals("4.5", LogikaResep.formatAngka(4.5))
        assertEquals("0.33", LogikaResep.formatAngka(1.0 / 3))
        assertEquals("100", LogikaResep.formatAngka(100.0))
        assertEquals("< 0.01", LogikaResep.formatAngka(0.001))
    }

    @Test fun parseJumlah() {
        assertEquals(1.5, LogikaResep.parseJumlah("1,5")!!, 0.0)
        listOf("abc", "0", "-1", "NaN", "Infinity", "1e999").forEach {
            assertNull(it, LogikaResep.parseJumlah(it))
        }
    }
}
