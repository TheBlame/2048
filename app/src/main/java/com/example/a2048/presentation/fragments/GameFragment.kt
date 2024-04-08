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
import com.example.a2048.util.IDialogListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
            args = it.getParcelable(ARG_PARAM1)

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
                    binding.gameField.game = it.game
                    binding.scoreValue.text = it.game.score.toString()
                    binding.recordValue.text = it.topScore.toString()
                    if (it.game.score > it.topScore) {
                        binding.recordValue.text = it.game.score.toString()
                    }
                    if (it.game.gameOver) {
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
        fun newInstance(param1: GameMode) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}