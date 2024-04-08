package com.example.a2048.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.a2048.R
import com.example.a2048.databinding.FragmentMainMenuBinding
import com.example.a2048.domain.entity.GameSetting
import com.google.android.material.card.MaterialCardView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenuFragment : Fragment() {
    private var _binding: FragmentMainMenuBinding? = null
    private val binding: FragmentMainMenuBinding
        get() = _binding ?: throw RuntimeException("MainMenuFragment == null")

    private var gameSetting: GameSetting = GameSetting(4, 4)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSquareMode()
        binding.groupSquareMode.referencedIds.forEach { id ->
            binding.root.findViewById<MaterialCardView>(id).setOnClickListener {
                resetSquareGroupCheck()
                binding.root.findViewById<MaterialCardView>(id).isChecked = true
                gameSetting = when (binding.root.findViewById<MaterialCardView>(id)) {
                    binding.card1 -> GameSetting(4, 4)
                    binding.card2 -> GameSetting(5, 5)
                    binding.card3 -> GameSetting(6, 6)
                    binding.card4 -> GameSetting(8, 8)
                    else -> throw Exception("Unknown card id")
                }
            }
        }
        binding.groupRectangleMode.referencedIds.forEach { id ->
            binding.root.findViewById<MaterialCardView>(id).setOnClickListener {
                resetRectangleGroupCheck()
                binding.root.findViewById<MaterialCardView>(id).isChecked = true
                gameSetting = when (binding.root.findViewById<MaterialCardView>(id)) {
                    binding.rectangleCard1 -> GameSetting(5, 3)
                    binding.rectangleCard2 -> GameSetting(6, 4)
                    binding.rectangleCard3 -> GameSetting(8, 5)
                    binding.rectangleCard4 -> GameSetting(9, 6)
                    else -> throw Exception("Unknown card id")
                }
            }
        }

        binding.groupModeSelect.referencedIds.forEach { id ->
            binding.root.findViewById<MaterialCardView>(id).setOnClickListener { _ ->
                binding.groupModeSelect.referencedIds.forEach {
                    binding.root.findViewById<MaterialCardView>(it).isChecked = false
                }
                binding.root.findViewById<MaterialCardView>(id).isChecked = true
                when (binding.root.findViewById<MaterialCardView>(id)) {
                    binding.mode1 -> {
                        initSquareMode()
                        resetRectangleGroupCheck()
                    }
                    binding.mode2 -> {
                        initRectangleMode()
                        resetSquareGroupCheck()
                    }
                }
            }
        }
        binding.playButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GameFragment.newInstance(gameSetting))
                .addToBackStack(null)
                .commit()
        }
    }

    private fun initSquareMode() {
        binding.groupSquareMode.visibility = VISIBLE
        binding.groupRectangleMode.visibility = GONE
        binding.card1.isChecked = true
        binding.mode1.isChecked = true
        gameSetting = GameSetting(4, 4)
    }

    private fun initRectangleMode() {
        binding.groupSquareMode.visibility = GONE
        binding.groupRectangleMode.visibility = VISIBLE
        binding.rectangleCard1.isChecked = true
        binding.mode2.isChecked = true
        gameSetting = GameSetting(5, 3)
    }

    private fun resetSquareGroupCheck() {
        binding.groupSquareMode.referencedIds.forEach {
            binding.root.findViewById<MaterialCardView>(it).isChecked = false
        }
    }

    private fun resetRectangleGroupCheck() {
        binding.groupRectangleMode.referencedIds.forEach {
            binding.root.findViewById<MaterialCardView>(it).isChecked = false
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}