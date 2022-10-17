package com.vinatti.datasales.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vinatti.datasales.R
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseMarketing
import com.vinatti.datasales.databinding.ItemChannelBinding
import com.vinatti.datasales.databinding.ItemMarketingBinding
import com.vinatti.datasales.utils.setSingleClick

class ListChannelAdapter(private val listContent:ArrayList<ResponseChannel.ChannelResponse>,
                         val context: Context,
                         val itemClick : ((ResponseChannel.ChannelResponse)->Unit)) : RecyclerView.Adapter<ListChannelAdapter.ListChannelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListChannelViewHolder {
        val binding = DataBindingUtil.inflate<ItemChannelBinding>(
            LayoutInflater.from(context),
            R.layout.item_channel,
            parent,
            false
        )
        return ListChannelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListChannelViewHolder, position: Int) {
        val item = listContent[position]
        holder.binData(item)

    }

    override fun getItemCount(): Int  = listContent.size

    inner class ListChannelViewHolder(val binding:ItemChannelBinding) : RecyclerView.ViewHolder(binding.root){

        fun binData(item:ResponseChannel.ChannelResponse){
            binding.apply {
                tvChannel.text = item.name

                if (item.selected){
                    imgTick.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_tick))
                }else{
                    imgTick.background = ContextCompat.getDrawable(context,R.drawable.bg_circle_tick)
                    imgTick.setImageDrawable(null)
                }

                rootView.setSingleClick {
                    if (!item.selected){
                        item.selected = true
                        notifyItemChanged(this@ListChannelViewHolder.bindingAdapterPosition)
                        resetSelected(this@ListChannelViewHolder.bindingAdapterPosition)
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