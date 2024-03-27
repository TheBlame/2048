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
import com.example.a2048.domain.entity.GameSetting
import com.example.a2048.presentation.fragments.GameFragment.DialogType.GAME_OVER
import com.example.a2048.presentation.fragments.GameFragment.DialogType.RESTART_GAME
import com.example.a2048.util.DialogListener
import com.example.a2048.util.Helpers.Companion.lazyViewModel
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
class GameFragment : Fragment(), DialogListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("GameFragment == null")

    private val component by lazy {
        (requireActivity().application as App2048).component
    }

    private val viewModel by lazyViewModel { savedStateHandle ->
        component.gameViewModel().create(GameSetting(4, 4), savedStateHandle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
                    binding.gameField.game = it
                    binding.scoreValue.text = it.score.toString()
                    if (it.gameOver) showDialog(GAME_OVER)
                }
            }
        }
        binding.gameField.setSwipeListener {
            viewModel.swipeField(it)
        }
        binding.retryButton.setOnClickListener {
            showDialog(RESTART_GAME)
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

    }

    enum class DialogType {
        RESTART_GAME, GAME_OVER
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}