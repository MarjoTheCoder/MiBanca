package com.example.mibanca.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mibanca.databinding.ItemBeneficiarioBinding
import com.example.mibanca.model.Beneficiary

class BeneficiariosAdapter(
    private var lista: List<Beneficiary>,
    private val onElementoClick: (Beneficiary) -> Unit,
    private val onMenuMoreClick: (Beneficiary) -> Unit
) : RecyclerView.Adapter<BeneficiariosAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBeneficiarioBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemBeneficiarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(b)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        val nombreCompleto = "${item.name.orEmpty()} ${item.lastName.orEmpty()}".trim()
        holder.binding.tvNombre.text = if (nombreCompleto.isEmpty()) "Sin nombre" else nombreCompleto

        val bancoReal = item.bankName ?: "Banco"
        val cuentaCorta = item.accountNumber.orEmpty().takeLast(4)
        holder.binding.tvBanco.text = "$bancoReal • **** $cuentaCorta"

        holder.binding.root.setOnClickListener {
            onElementoClick(item)
        }

        holder.binding.btnMore.setOnClickListener {
            onMenuMoreClick(item)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun filtrarLista(nuevaListaFiltrada: List<Beneficiary>) {
        this.lista = nuevaListaFiltrada
        notifyDataSetChanged()
    }
}