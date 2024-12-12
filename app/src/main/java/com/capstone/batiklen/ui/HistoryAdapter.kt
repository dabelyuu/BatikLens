package com.capstone.batiklen.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.batiklen.R
import com.capstone.batiklen.data.remote.response.BatikItem
import com.capstone.batiklen.data.remote.response.DataItem
import com.capstone.batiklen.databinding.ItemBatikBinding
import com.capstone.batiklen.ui.detail.DetailActivity
import com.capstone.batiklen.ui.detail.DetailHistoryActivity

class HistoryAdapter(
    private val context: Context?,
) : ListAdapter<DataItem, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    class HistoryViewHolder(private val binding: ItemBatikBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, batik: DataItem) {
            binding.namaBatik.text = batik.history?.result
            binding.sejarahBatik.text = context.getString(R.string.confidence_score, batik.history?.confidenceScore.toString())
            Glide.with(itemView.context).load(batik.imageUrl?.url?.get(0)).into(binding.imageUrl)
//            binding.filosofiBatik.text = batik.metadata?.sejarahBatik
//            binding.asalBatik.text = batik.metadata?.filosofiBatik

            // Set click listener
            binding.root.setOnClickListener {
                val intent = Intent(context, DetailHistoryActivity::class.java)
                intent.putExtra("HISTORY_ITEM", batik)
                context.startActivity(intent)
            }

//            binding.dropdownIcon.setOnClickListener {
//                val isVisible = binding.expandableSection.visibility == View.VISIBLE
//                binding.expandableSection.visibility = if (isVisible) View.GONE else View.VISIBLE
//                val iconRes = if (isVisible) R.drawable.ic_arrow_down_24 else R.drawable.ic_arrow_up_24
//                binding.dropdownIcon.setImageResource(iconRes)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemBatikBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val batikData =getItem(position)
        holder.bind(context!!, batikData)
    }


    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DataItem> =
            object : DiffUtil.ItemCallback<DataItem>() {
                override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                    return oldItem.history?.id == newItem.history?.id
                }
                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: DataItem, newItem:DataItem): Boolean {
                    return oldItem.history?.id == newItem.history?.id
                }
            }
    }
}