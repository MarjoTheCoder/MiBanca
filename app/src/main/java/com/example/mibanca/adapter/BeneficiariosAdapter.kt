package com.example.mibanca.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mibanca.databinding.ItemBeneficiarioBinding
import com.example.mibanca.model.Beneficiary

class BeneficiariosAdapter(
    private val lista: List<Beneficiary>,
    private val onCentinelaClick: (Beneficiary) -> Unit
) : RecyclerView.Adapter<BeneficiariosAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBeneficiarioBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemBeneficiarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(b)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.binding.tvNombre.text = "${item.name} ${item.lastName}"

        val bancoReal = item.bankName ?: "Banco"
        val cuentaCorta = item.accountNumber.orEmpty().takeLast(4)
        holder.binding.tvBanco.text = "$bancoReal • **** $cuentaCorta"

        holder.binding.root.setOnClickListener {
            onCentinelaClick(item)
        }

        holder.binding.btnMore.setOnClickListener {
            onCentinelaClick(item)
        }
    }

    override fun getItemCount(): Int = lista.size
}