package com.vinatti.datasales.ui.fragment.marketing

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

class MarketingAdapter(val context: Context,val itemClick : ((ResponseMarketing.MarketingResponse,Int)->Unit)) : RecyclerView.Adapter<MarketingAdapter.MarketingViewHolder>() {

    private val listContent = arrayListOf<ResponseMarketing.MarketingResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketingViewHolder {
        val binding = DataBindingUtil.inflate<ItemMarketingBinding>(
            LayoutInflater.from(context),
            R.layout.item_marketing,
            parent,
            false
        )
        return MarketingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketingViewHolder, position: Int) {
        val item = listContent[position]
        holder.binData(item)
    }

    override fun getItemCount(): Int = listContent.size

    fun updateListContent(newList: List<ResponseMarketing.MarketingResponse>) {
        listContent.clear()
        listContent.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearAll(){
        try {
            listContent.clear()
            notifyDataSetChanged()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    inner class MarketingViewHolder(val binding: ItemMarketingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun binData(item: ResponseMarketing.MarketingResponse) {
            binding.apply {
                try {

                    tvTimeDate.text = "${item.fromDate} - ${item.toDate}"
                    tvMarketing.text = item.channelName
                    tvPriceTags.text = "${AppUtils.formatPriceNumber(item.cost.toLong())} đ"
                    tvAmount.text = "${AppUtils.formatPriceNumber(item.totalAmount.toLong())} đ"
                    tvCountCustom.text = item.quantity.toString()

                    rootView.setSingleClick {
                        Log.e("TAG", "binData: 1", )
                        itemClick(item,this@MarketingViewHolder.bindingAdapterPosition)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }


            }

        }

    }
}