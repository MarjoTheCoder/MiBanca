package com.example.mibanca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mibanca.R
import com.example.mibanca.model.Transaction

class MovimientosAdapter(private val movimientos: List<Transaction>) :
    RecyclerView.Adapter<MovimientosAdapter.MovimientoViewHolder>() {

    class MovimientoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvConcepto: TextView = view.findViewById(R.id.tvConceptoMovimiento)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaMovimiento)
        val tvMonto: TextView = view.findViewById(R.id.tvMontoMovimiento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovimientoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movimiento_mock, parent, false)
        return MovimientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovimientoViewHolder, position: Int) {
        val movimiento = movimientos[position]

        val desc = movimiento.description
        holder.tvConcepto.text = if (desc.isNullOrEmpty()) "Transferencia realizada" else desc

        val fechaRaw = movimiento.date
        holder.tvFecha.text = when (fechaRaw) {
            is String -> fechaRaw
            null -> "Fecha no disponible"
            else -> "Reciente"
        }

        holder.tvMonto.text = "- ${movimiento.getFormattedAmount()}"
    }

    override fun getItemCount(): Int = movimientos.size
}