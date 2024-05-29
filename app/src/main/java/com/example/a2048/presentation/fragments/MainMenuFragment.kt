package com.example.a2048.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import com.example.a2048.App2048
import com.example.a2048.R
import com.example.a2048.databinding.FragmentMainMenuBinding
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.entity.GameMode.MODE3x5
import com.example.a2048.domain.entity.GameMode.MODE4x4
import com.example.a2048.domain.entity.GameMode.MODE4x6
import com.example.a2048.domain.entity.GameMode.MODE5x5
import com.example.a2048.domain.entity.GameMode.MODE5x8
import com.example.a2048.domain.entity.GameMode.MODE6x6
import com.example.a2048.domain.entity.GameMode.MODE6x9
import com.example.a2048.domain.entity.GameMode.MODE8x8
import com.example.a2048.util.Helpers.Companion.lazyViewModel
import com.google.android.material.card.MaterialCardView

class MainMenuFragment : Fragment() {
    private var _binding: FragmentMainMenuBinding? = null
    private val binding: FragmentMainMenuBinding
        get() = _binding ?: throw RuntimeException("MainMenuFragment == null")

    private lateinit var gameMode: GameMode

    private val component by lazy {
        (requireActivity().application as App2048).component
    }

    private val viewModel by lazyViewModel { savedStateHandle ->
        component.mainMenuViewModel().create(savedStateHandle)
    }

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
        viewModel.scoreList
        binding.groupSquareMode.referencedIds.forEach { id ->
            binding.root.findViewById<MaterialCardView>(id).setOnClickListener {
                resetSquareGroupCheck()
                binding.root.findViewById<MaterialCardView>(id).isChecked = true
                gameMode = when (binding.root.findViewById<MaterialCardView>(id)) {
                    binding.squareCard1 -> MODE4x4
                    binding.squareCard2 -> MODE5x5
                    binding.squareCard3 -> MODE6x6
                    binding.squareCard4 -> MODE8x8
                    else -> throw Exception("Unknown card id")
                }
            }
        }
        binding.groupRectangleMode.referencedIds.forEach { id ->
            binding.root.findViewById<MaterialCardView>(id).setOnClickListener {
                resetRectangleGroupCheck()
                binding.root.findViewById<MaterialCardView>(id).isChecked = true
                gameMode = when (binding.root.findViewById<MaterialCardView>(id)) {
                    binding.rectangleCard1 -> MODE3x5
                    binding.rectangleCard2 -> MODE4x6
                    binding.rectangleCard3 -> MODE5x8
                    binding.rectangleCard4 -> MODE6x9
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
                    binding.squareMode -> {
                        initSquareMode()
                        resetRectangleGroupCheck()
                    }

                    binding.rectangleMode -> {
                        initRectangleMode()
                        resetSquareGroupCheck()
                    }
                }
            }
        }
        binding.playButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GameFragment.newInstance(gameMode))
                .addToBackStack(null)
                .commit()
        }
        binding.leaderboardButton.setOnClickListener {
            showLeaderboardDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLeaderboardDialog() {
        val dialog = LeaderboardDialogFragment.newInstance(gameMode)
        dialog.show(childFragmentManager, LeaderboardDialogFragment.TAG)
    }

    private fun initSquareMode() {
        if (!checkGroupForAnyChecked(binding.groupSquareMode)) {
            binding.groupSquareMode.visibility = VISIBLE
            binding.groupRectangleMode.visibility = GONE
            binding.squareCard1.isChecked = true
            binding.squareMode.isChecked = true
            gameMode = MODE4x4
        }
    }

    private fun initRectangleMode() {
        if (!checkGroupForAnyChecked(binding.groupRectangleMode)) {
            binding.groupSquareMode.visibility = GONE
            binding.groupRectangleMode.visibility = VISIBLE
            binding.rectangleCard1.isChecked = true
            binding.rectangleMode.isChecked = true
            gameMode = MODE3x5
        }
    }

    private fun checkGroupForAnyChecked(group: Group): Boolean {
        return group.referencedIds.any {
            binding.root.findViewById<MaterialCardView>(it).isChecked
        }
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
}