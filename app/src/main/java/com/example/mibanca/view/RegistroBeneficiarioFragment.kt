package com.example.mibanca.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mibanca.data.repository.BankingRepository // Importación añadida
import com.example.mibanca.databinding.FragmentRegistroBeneficiarioBinding
import com.example.mibanca.model.AddBeneficiaryRequest
import com.example.mibanca.model.Beneficiary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        // Leemos si nos mandaron un beneficiario de la lista anterior
        datosRecibidos = arguments?.getSerializable("KEY_BENEFICIARIO") as? Beneficiary

        if (datosRecibidos != null) {
            // LECTURA / EDICIÓN
            binding.tvFormTitle.text = "Detalle del Beneficiario"
            binding.etNombreBeneficiario.setText(datosRecibidos!!.name)
            binding.etApellidoBeneficiario.setText(datosRecibidos!!.lastName)
            binding.etAliasBeneficiario.setText(datosRecibidos!!.alias)
            binding.etBancoBeneficiario.setText(datosRecibidos!!.bankName)
            binding.etCuentaBeneficiario.setText(datosRecibidos!!.accountNumber)
            binding.btnGuardarBeneficiario.text = "Actualizar información"
            binding.btnEliminarBeneficiario.visibility = View.VISIBLE

            binding.btnGuardarBeneficiario.setOnClickListener {
                val nom = binding.etNombreBeneficiario.text.toString().trim()
                val ape = binding.etApellidoBeneficiario.text.toString().trim()
                val ali = binding.etAliasBeneficiario.text.toString().trim()
                val ban = binding.etBancoBeneficiario.text.toString().trim()
                val cue = binding.etCuentaBeneficiario.text.toString().trim()

                if (nom.isEmpty() || ape.isEmpty() || ali.isEmpty() || ban.isEmpty() || cue.isEmpty()) return@setOnClickListener

                val request = AddBeneficiaryRequest(name = nom, lastName = ape, alias = ali, accountNumber = cue, bankName = ban)

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        // REPARADO: Ahora llama a la capa del repositorio
                        val res = withContext(Dispatchers.IO) {
                            repository.updateBeneficiary(datosRecibidos!!.id, request)
                        }
                        if (res.isSuccessful) {
                            Toast.makeText(requireContext(), "¡Información actualizada!", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp() // Regresa a la lista
                        } else {
                            Toast.makeText(requireContext(), "Error del servidor al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            binding.btnEliminarBeneficiario.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        // REPARADO: Ahora llama a la capa del repositorio
                        val res = withContext(Dispatchers.IO) {
                            repository.deleteBeneficiary(datosRecibidos!!.id)
                        }
                        if (res.isSuccessful) {
                            Toast.makeText(requireContext(), "¡Beneficiario eliminado!", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp() // Regresa a la lista
                        } else {
                            Toast.makeText(requireContext(), "Error del servidor al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // CREACIÓN
            binding.tvFormTitle.text = "Nuevo Beneficiario"
            binding.btnGuardarBeneficiario.text = "Guardar beneficiario"
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

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // REPARADO: Se adapta a la firma de 'addBeneficiary' declarada en tu BankingRepository
                // pasándole los parámetros sueltos en lugar del objeto Request completo.
                val res = withContext(Dispatchers.IO) {
                    repository.addBeneficiary(
                        name = nom,
                        lastName = ape,
                        alias = ali,
                        accountNumber = cue,
                        bankName = ban
                    )
                }
                if (res.isSuccessful) {
                    Toast.makeText(requireContext(), "¡Beneficiario Agregado!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // Regresa a la lista
                } else {
                    android.util.Log.e("CRUD_ERROR", "Error del servidor: Código ${res.code()} - ${res.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Servidor rechazó la petición: ${res.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("CRUD_ERROR", "Excepción atrapada al guardar: ${e.message}", e)
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}