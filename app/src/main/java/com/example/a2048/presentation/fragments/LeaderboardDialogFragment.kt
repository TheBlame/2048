package com.example.a2048.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.a2048.R
import com.example.a2048.databinding.LeaderboardDialogBinding
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.presentation.adapters.ScoreListAdapter
import com.example.a2048.presentation.viewmodels.MainMenuViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [LeaderboardDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LeaderboardDialogFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var gameMode: GameMode? = null

    private var _binding: LeaderboardDialogBinding? = null
    private val binding: LeaderboardDialogBinding
        get() = _binding ?: throw RuntimeException("LeaderboardDialogBinding == null")

    private val viewModel: MainMenuViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private val adapter by lazy {
        ScoreListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameMode = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LeaderboardDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                gameMode?.let { viewModel.getScoreList(it) }
                binding.modeName.text = gameMode?.modeName()
                binding.scoreList.adapter = adapter
                binding.scoreList.addItemDecoration(DividerItemDecoration(activity, RecyclerView.VERTICAL))
                viewModel.scoreList.collectLatest {
                    if (it.isEmpty()) {
                        binding.noRecord.visibility = VISIBLE
                    } else {
                        adapter.submitList(it)
                    }
                }
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(gameMode: GameMode) =
            LeaderboardDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, gameMode)
                }
            }

        const val TAG = "LeaderboardDialogFragment"
    }
}