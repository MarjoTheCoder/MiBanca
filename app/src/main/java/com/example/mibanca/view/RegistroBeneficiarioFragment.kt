package com.example.mibanca.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mibanca.data.repository.BankingRepository
import com.example.mibanca.databinding.FragmentRegistroBeneficiarioBinding
import com.example.mibanca.model.AddBeneficiaryRequest
import com.example.mibanca.model.Beneficiary
import com.example.mibanca.network.apiCall // Importamos el helper unificado de red
import kotlinx.coroutines.launch

class RegistroBeneficiarioFragment : Fragment() {
    private var _binding: FragmentRegistroBeneficiarioBinding? = null
    private val binding get() = _binding!!
    private var datosRecibidos: Beneficiary? = null

    private val repository: BankingRepository = com.example.mibanca.data.repository.BankingRepositoryImpl(
        com.example.mibanca.di.NetworkModule.apiService
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentRegistroBeneficiarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datosRecibidos = arguments?.getSerializable("KEY_BENEFICIARIO") as? Beneficiary

        if (datosRecibidos != null) {
            binding.tvFormTitle.text = "Detalle del Beneficiario"
            binding.etNombreBeneficiario.setText(datosRecibidos!!.name)
            binding.etApellidoBeneficiario.setText(datosRecibidos!!.lastName)
            binding.etAliasBeneficiario.setText(datosRecibidos!!.alias)
            binding.etBancoBeneficiario.setText(datosRecibidos!!.bankName)
            binding.etCuentaBeneficiario.setText(datosRecibidos!!.accountNumber)

            binding.btnGuardarBeneficiario.visibility = View.GONE
            binding.btnEliminarBeneficiario.visibility = View.VISIBLE

            binding.etNombreBeneficiario.isEnabled = false
            binding.etApellidoBeneficiario.isEnabled = false
            binding.etAliasBeneficiario.isEnabled = false
            binding.etBancoBeneficiario.isEnabled = false
            binding.etCuentaBeneficiario.isEnabled = false

            binding.btnEliminarBeneficiario.setOnClickListener {
                eliminarBeneficiarioDeAPI(datosRecibidos!!.id)
            }

        } else {
            binding.tvFormTitle.text = "Nuevo Beneficiario"
            binding.btnGuardarBeneficiario.text = "Guardar beneficiario"
            binding.btnGuardarBeneficiario.visibility = View.VISIBLE
            binding.btnEliminarBeneficiario.visibility = View.GONE

            binding.btnGuardarBeneficiario.setOnClickListener { guardarNuevoEnAPI() }
        }
    }

    private fun guardarNuevoEnAPI() {
        val nom = binding.etNombreBeneficiario.text.toString().trim()
        val ape = binding.etApellidoBeneficiario.text.toString().trim()
        val ali = binding.etAliasBeneficiario.text.toString().trim()
        val ban = binding.etBancoBeneficiario.text.toString().trim()
        val cue = binding.etCuentaBeneficiario.text.toString().trim()

        if (nom.isEmpty() || ape.isEmpty() || ali.isEmpty() || ban.isEmpty() || cue.isEmpty()) {
            Toast.makeText(requireContext(), "Faltan campos por llenar", Toast.LENGTH_SHORT).show()
            return
        }

        val request = AddBeneficiaryRequest(
            name = nom,
            lastName = ape,
            alias = ali,
            accountNumber = cue,
            bankName = ban
        )

        viewLifecycleOwner.lifecycleScope.launch {
            apiCall { repository.addBeneficiary(request) }
                .onSuccess { beneficiarioCreado ->
                    Toast.makeText(requireContext(), "¡Beneficiario Agregado!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .onFailure { error ->
                    android.util.Log.e("CRUD_ERROR", "Fallo detectado: ${error.message}")
                    Toast.makeText(requireContext(), "Error al guardar: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun eliminarBeneficiarioDeAPI(idBeneficiario: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = repository.deleteBeneficiary(idBeneficiario)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "¡Beneficiario eliminado con éxito!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "El servidor rechazó la eliminación", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("CRUD_ERROR", "Error al borrar: ${e.message}")
                Toast.makeText(requireContext(), "Fallo de red al eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}