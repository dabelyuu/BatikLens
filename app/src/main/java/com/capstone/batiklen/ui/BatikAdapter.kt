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
import com.capstone.batiklen.databinding.ItemBatikBinding
import com.capstone.batiklen.ui.detail.DetailActivity
import com.capstone.batiklen.utils.UserPreferences
import com.capstone.batiklen.utils.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BatikAdapter(
    private val context: Context?,
//    private val onDetailClick : (BatikItem) -> Unit
) : ListAdapter<BatikItem, BatikAdapter.BatikViewHolder>(DIFF_CALLBACK) {

//    private val userPreferences: UserPreferences by lazy {
//        UserPreferences.getInstance(context?.dataStore!!)
//    }

    class BatikViewHolder(private val binding: ItemBatikBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context,batik: BatikItem) {



            binding.namaBatik.text = batik.namaBatik
            binding.sejarahBatik.text = batik.asalBatik
            Glide.with(itemView.context).load(batik.imageUrl?.url?.get(0)).into(binding.imageUrl)
//            binding.filosofiBatik.text = batik.sejarahBatik
//            binding.asalBatik.text = batik.filosofiBatik

            // Set click listener
            binding.root.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("BATIK_ITEM", batik)
                context.startActivity(intent)
            }
//
//            binding.dropdownIcon.setOnClickListener {
//                val isVisible = binding.expandableSection.visibility == View.VISIBLE
//                binding.expandableSection.visibility = if (isVisible) View.GONE else View.VISIBLE
//                val iconRes = if (isVisible) R.drawable.ic_arrow_down_24 else R.drawable.ic_arrow_up_24
//                binding.dropdownIcon.setImageResource(iconRes)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatikViewHolder {
        val binding = ItemBatikBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BatikViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BatikViewHolder, position: Int) {
        val batikData =getItem(position)
        holder.bind(context!!, batikData)

//        holder.itemView.setOnClickListener{
//            onDetailClick(BatikItem())
//        }
    }


    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<BatikItem> =
            object : DiffUtil.ItemCallback<BatikItem>() {
                override fun areItemsTheSame(oldItem: BatikItem, newItem: BatikItem): Boolean {
                    return oldItem.id == newItem.id
                }
                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: BatikItem, newItem: BatikItem): Boolean {
                    return oldItem.id == newItem.id
                }
            }
    }
}
