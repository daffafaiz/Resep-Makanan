package com.resepku.app.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.resepku.app.R
import com.resepku.app.data.ResepRepository
import com.resepku.app.databinding.ItemResepBinding
import com.resepku.app.model.Resep

class ResepAdapter(
    private val onKlik: (Resep) -> Unit,
    private val onTandai: (Resep) -> Unit
) : RecyclerView.Adapter<ResepAdapter.Holder>() {

    private var data: List<Resep> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun kirim(baru: List<Resep>) {
        data = baru
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemResepBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(data[position])

    inner class Holder(private val b: ItemResepBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(r: Resep) {
            val c = b.root.context
            b.tvJudul.text = r.judul
            b.tvKategori.text = ResepRepository.namaKategori(r.kategoriId)
            b.tvLabelWaktu.pasangLabelWaktu(r.waktuMenit)
            b.tvWaktu.text = c.getString(R.string.format_menit, r.waktuMenit)
            b.tvPorsi.text = c.getString(R.string.format_porsi, r.porsiDasar)
            b.tvBahan.text = c.getString(R.string.format_bahan, ResepRepository.bahan(r.id).size)
            b.btnTandai.setImageResource(
                if (r.mauDimasak) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border
            )
            b.btnTandai.contentDescription =
                c.getString(if (r.mauDimasak) R.string.lepas_tanda else R.string.tandai_mau_dimasak)
            b.btnTandai.setOnClickListener { onTandai(r) }
            b.root.setOnClickListener { onKlik(r) }
        }
    }
}
