package com.example.a2048.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a2048.databinding.ScoreItemBinding
import com.example.a2048.domain.entity.GameScore

class ScoreListAdapter :
    ListAdapter<GameScore, ScoreListAdapter.ScoreListViewHolder>(DiffCallback) {

    class ScoreListViewHolder(val binding: ScoreItemBinding) : RecyclerView.ViewHolder(binding.root)

    private object DiffCallback : DiffUtil.ItemCallback<GameScore>() {
        override fun areItemsTheSame(oldItem: GameScore, newItem: GameScore): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GameScore, newItem: GameScore): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreListViewHolder {
        val binding = ScoreItemBinding.inflate(LayoutInflater.from(parent.context))
        return ScoreListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoreListViewHolder, position: Int) {
        val score = getItem(position)
        holder.binding.itemNumber.text = "${position + 1}."
        holder.binding.itemDate.text = score.date
        holder.binding.itemScore.text = score.value.toString()
    }
}