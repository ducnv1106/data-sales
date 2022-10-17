package com.vinatti.datasales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vinatti.datasales.R
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseListRole
import com.vinatti.datasales.databinding.ItemChannelBinding
import com.vinatti.datasales.databinding.ItemRoleBinding
import com.vinatti.datasales.utils.setSingleClick

class ListRoleAdapter(private val listContent:ArrayList<ResponseListRole.RoleResponse>,
                         val context: Context,
                         val itemClick : ((ResponseListRole.RoleResponse)->Unit)) : RecyclerView.Adapter<ListRoleAdapter.ListRoleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRoleViewHolder {
        val binding = DataBindingUtil.inflate<ItemRoleBinding>(
            LayoutInflater.from(context),
            R.layout.item_role,
            parent,
            false
        )
        return ListRoleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListRoleViewHolder, position: Int) {
        val item = listContent[position]
        holder.binData(item)

    }

    override fun getItemCount(): Int  = listContent.size

    inner class ListRoleViewHolder(val binding: ItemRoleBinding) : RecyclerView.ViewHolder(binding.root){

        fun binData(item: ResponseListRole.RoleResponse){
            binding.apply {
                tvChannel.text = item.name

                if (item.selected){
                    imgTick.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_tick))
                }else{
                    imgTick.background = ContextCompat.getDrawable(context, R.drawable.bg_circle_tick)
                    imgTick.setImageDrawable(null)
                }

                rootView.setSingleClick {
                    if (!item.selected){
                        item.selected = true
                        notifyItemChanged(this@ListRoleViewHolder.bindingAdapterPosition)
                        resetSelected(this@ListRoleViewHolder.bindingAdapterPosition)
                    }
                    itemClick(item)
                }
            }

        }

    }
    fun resetSelected(position: Int){
        for (i in 0 until listContent.size){
            if (i != position && listContent[i].selected){
                listContent[i].selected = false
                notifyItemChanged(i)
            }
        }

    }

}