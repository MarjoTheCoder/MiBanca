package com.example.mibanca.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mibanca.R
import com.example.mibanca.databinding.FragmentBeneficiariosBinding
import com.example.mibanca.model.Beneficiary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.mibanca.adapter.BeneficiariosAdapter
class BeneficiariosFragment : Fragment() {
    private var _binding: FragmentBeneficiariosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentBeneficiariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddBeneficiario.setOnClickListener { irAFormulario(null) }
        binding.btnAgregarPrimero.setOnClickListener { irAFormulario(null) }

        cargarBeneficiariosDesdeAPI()
    }

    private fun cargarBeneficiariosDesdeAPI() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    com.example.mibanca.di.NetworkModule.apiService.getBeneficiaries()
                }
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    binding.rvBeneficiarios.visibility = View.VISIBLE
                    binding.layoutEmptyBeneficiarios.visibility = View.GONE

                    binding.rvBeneficiarios.adapter = BeneficiariosAdapter(response.body()!!) { click ->
                        irAFormulario(click)
                    }
                } else {
                    binding.rvBeneficiarios.visibility = View.GONE
                    binding.layoutEmptyBeneficiarios.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                binding.rvBeneficiarios.visibility = View.GONE
                binding.layoutEmptyBeneficiarios.visibility = View.VISIBLE
            }
        }
    }

    private fun irAFormulario(beneficiario: Beneficiary?) {
        val bundle = Bundle().apply { putSerializable("KEY_BENEFICIARIO", beneficiario) }
        findNavController().navigate(R.id.fragment_registro_beneficiario, bundle)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}