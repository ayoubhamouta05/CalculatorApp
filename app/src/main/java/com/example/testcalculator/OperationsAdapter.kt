package com.example.testcalculator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testcalculator.databinding.RowOperationsBinding
import javax.security.auth.callback.Callback

class OperationsAdapter() : RecyclerView.Adapter<OperationsAdapter.OperationsViewHolder>() {
    inner class OperationsViewHolder (var binding: RowOperationsBinding) : RecyclerView.ViewHolder(binding.root)
    private val differCallback = object : DiffUtil.ItemCallback <String>(){

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
     var differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationsViewHolder {
        return OperationsViewHolder(
            RowOperationsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: OperationsViewHolder, position: Int) {
        holder.binding.apply {
            operationTv.text = differ.currentList[position]
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}