package com.example.a2048.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.a2048.App2048
import com.example.a2048.databinding.FragmentGameBinding
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.presentation.fragments.GameFragment.DialogType.GAME_OVER
import com.example.a2048.presentation.fragments.GameFragment.DialogType.RESTART_GAME
import com.example.a2048.util.Helpers.Companion.lazyViewModel
import com.example.a2048.util.Helpers.Companion.parcelable
import com.example.a2048.util.IDialogListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"

class GameFragment : Fragment(), IDialogListener {
    // TODO: Rename and change types of parameters
    private var args: GameMode? = null

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("GameFragment == null")

    private val component by lazy {
        (requireActivity().application as App2048).component
    }

    private val viewModel by lazyViewModel { savedStateHandle ->
        component.gameViewModel().create(args, savedStateHandle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            args = it.parcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collectLatest {
                    binding.scoreValue.text = it.score.toString()
                    binding.recordValue.text = it.topScore.toString()
                    binding.gameField.game = it
                    if (it.score > it.topScore) {
                        binding.recordValue.text = it.score.toString()
                    }
                    if (it.gameOver) {
                        showDialog(GAME_OVER)
                        viewModel.saveScore()
                    }
                }
            }
        }
        binding.gameField.setSwipeListener {
            viewModel.swipeField(it)
        }
        binding.retryButton.setOnClickListener {
            showDialog(RESTART_GAME)
        }
        binding.homeButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun showDialog(dialogType: DialogType) {
        val dialog = when (dialogType) {
            RESTART_GAME -> GameRetryDialogFragment()

            GAME_OVER -> GameOverDialogFragment()
        }

        val tag = when (dialogType) {
            RESTART_GAME -> GameRetryDialogFragment.TAG

            GAME_OVER -> GameOverDialogFragment.TAG
        }

        dialog.isCancelable = false
        dialog.show(childFragmentManager, tag)
    }

    override fun onPositiveClick() {
        viewModel.startNewGame()
    }

    override fun onNegativeClick() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    enum class DialogType {
        RESTART_GAME, GAME_OVER
    }

    companion object {
        @JvmStatic
        fun newInstance(gameMode: GameMode) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, gameMode)
                }
            }
    }
}