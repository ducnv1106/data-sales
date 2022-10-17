package com.vinatti.datasales.ui.fragment.personal.manager_staff

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView.NO_POSITION

import androidx.recyclerview.widget.RecyclerView
import com.vinatti.datasales.R
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.data.api_entities.response.ResponseMarketing
import com.vinatti.datasales.databinding.ItemStaffBinding
import com.vinatti.datasales.utils.SwipeLayout
import com.vinatti.datasales.utils.SwipeLayout.SwipeActionsListener
import com.vinatti.datasales.utils.setSingleClick


class ManagerStaffAdapter(val context: Context,
                          val itemClick : ((ResponseManagerStaff.ManagerStaffResponse, Int)->Unit),
                          val itemDelete : ((ResponseManagerStaff.ManagerStaffResponse, Int)->Unit)  ) : RecyclerView.Adapter<ManagerStaffAdapter.ManagerStaffViewHolder>() {

    private val listContent = arrayListOf<ResponseManagerStaff.ManagerStaffResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagerStaffViewHolder {
        val binding = DataBindingUtil.inflate<ItemStaffBinding>(
            LayoutInflater.from(context),
            R.layout.item_staff,
            parent,
            false
        )
        return ManagerStaffViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ManagerStaffViewHolder, position: Int) {
        holder.binData(listContent[position])

    }

    override fun getItemCount(): Int = listContent.size

    fun updateList(newList:ArrayList<ResponseManagerStaff.ManagerStaffResponse>){
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


    inner class ManagerStaffViewHolder(val binding:ItemStaffBinding) : RecyclerView.ViewHolder(binding.root){

        fun binData(item:ResponseManagerStaff.ManagerStaffResponse){
            binding.apply {

                tvName.text = item.name
                tvPhone.text = item.mobileNumber
                tvRole.text = item.roleName
                
                if (item.isLock == "N"){
                    btnStatus.text = context.resources.getString(R.string.str_status_online)
                    btnStatus.setTextColor(context.getColor(R.color.colorBlack3))
                    btnStatus.background = ContextCompat.getDrawable(context,R.drawable.bg_btn_status_online)

                }  else  {
                    btnStatus.text = context.resources.getString(R.string.str_status_offline)
                    btnStatus.setTextColor(context.getColor(R.color.colorRed))
                    btnStatus.background = ContextCompat.getDrawable(context,R.drawable.bg_btn_status_offline)
                }

                dragItem.setSingleClick {
                    itemClick(item,this@ManagerStaffViewHolder.bindingAdapterPosition)
                }
                rightView.setSingleClick {
                    itemDelete(item,this@ManagerStaffViewHolder.bindingAdapterPosition)
                }

                swipeLayout.setOnActionsListener(object : SwipeActionsListener {
                    override fun onOpen(direction: Int, isContinuous: Boolean) {
                        if (direction == SwipeLayout.LEFT && isContinuous) {
                            if (adapterPosition !== NO_POSITION) {
                                remove(itemView.context, adapterPosition)
                            }
                        }
                    }

                    override fun onClose() {}
                })

            }

        }

    }

    private fun remove(context: Context, position: Int) {
        Toast.makeText(context, "removed item $position", Toast.LENGTH_SHORT).show()
    }

}