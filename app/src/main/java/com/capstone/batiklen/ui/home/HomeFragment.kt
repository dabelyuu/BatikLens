package com.capstone.batiklen.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.batiklen.data.Result
import com.capstone.batiklen.databinding.FragmentHomeBinding
import com.capstone.batiklen.ui.AuthViewModel
import com.capstone.batiklen.ui.BatikAdapter
import com.capstone.batiklen.ui.BatikViewModel
import com.capstone.batiklen.utils.ViewModelFactory
import com.capstone.batiklen.utils.dataStore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val batikViewModel by viewModels<BatikViewModel> {
        ViewModelFactory.getInstance(requireContext(), context?.dataStore!!)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val activeEventsAdapter = BatikAdapter(requireContext())

        batikViewModel.getAllBatik().observe(viewLifecycleOwner){result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBarLogin.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBarLogin.visibility = View.GONE
                    val data = result.data
                    activeEventsAdapter.submitList(data)
                }
                is Result.Error -> {
                    binding.progressBarLogin.visibility = View.GONE
                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.rvBatik.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = activeEventsAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}