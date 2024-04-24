package com.example.a2048.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
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
import com.example.a2048.util.Helpers.Companion.parcelable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"

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
            gameMode = it.parcelable(ARG_PARAM1)
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
                binding.modeName.text = context?.let { gameMode?.modeName(it) }
                binding.scoreListRecycler.adapter = adapter
                binding.scoreListRecycler.addItemDecoration(DividerItemDecoration(activity, RecyclerView.VERTICAL))
                viewModel.scoreList.collectLatest {
                    if (it.isEmpty()) {
                        binding.scoreList.visibility = GONE
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