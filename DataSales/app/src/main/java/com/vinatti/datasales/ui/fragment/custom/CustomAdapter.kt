package com.vinatti.datasales.ui.fragment.custom

import com.vinatti.datasales.data.api_entities.response.ResponseCustomSearch
import com.vinatti.datasales.databinding.ItemCustomBinding

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vinatti.datasales.R
import com.vinatti.datasales.data.api_entities.response.ResponseMarketing
import com.vinatti.datasales.databinding.ItemMarketingBinding
import com.vinatti.datasales.ui.activities.MainActivity
import com.vinatti.datasales.utils.AppUtils
import com.vinatti.datasales.utils.setSingleClick
import kotlinx.coroutines.MainScope

class CustomAdapter(val context: Context,
                    val itemClick : ((ResponseCustomSearch.CustomSearchResponse,Int)->Unit),
                    val itemDelete : ((ResponseCustomSearch.CustomSearchResponse,Int)->Unit)) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

    private val listContent = arrayListOf<ResponseCustomSearch.CustomSearchResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = DataBindingUtil.inflate<ItemCustomBinding>(
            LayoutInflater.from(context),
            R.layout.item_custom,
            parent,
            false
        )
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = listContent[position]
        holder.binData(item)
    }

    override fun getItemCount(): Int = listContent.size

    fun updateListContent(newList: List<ResponseCustomSearch.CustomSearchResponse>) {
        listContent.clear()
        listContent.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        try {
            listContent.removeAt(position)
            notifyItemRemoved(position)
            notifyItemChanged(listContent.size-1)
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    fun clearAll(){
        try {
            listContent.clear()
            notifyDataSetChanged()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    inner class CustomViewHolder(val binding: ItemCustomBinding) : RecyclerView.ViewHolder(binding.root) {

        fun binData(item: ResponseCustomSearch.CustomSearchResponse) {
            binding.apply {
                try {
                    tvName.text = item.name
                    tvPhone.text = item.mobileNumber
                    tvAddress.text = item.address
                    tvDocument.text = item.content
                    tvTimeDate.text = "${item.appointmentDate} ${item.appointmentTime} - ${item.appointmentInfo}"
                    tvMarketing.text = item.channelName
                    tvStatus.text = item.statusName
                    tvAssignedName.text = item.assignedName


                    dragItem.setSingleClick {
                        itemClick(item,this@CustomViewHolder.bindingAdapterPosition)
                    }
                    rightView.setSingleClick {
                        itemDelete(item,this@CustomViewHolder.bindingAdapterPosition)

                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }


            }

        }

    }
}