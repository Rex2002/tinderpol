package de.dhbw.tinderpol.util

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.dhbw.tinderpol.NoticeInfoFragment
import de.dhbw.tinderpol.R
import de.dhbw.tinderpol.SDO
import de.dhbw.tinderpol.data.Notice

class StarredNoticesListItemAdapter (private val context : Context, private var dataset: List<Notice>) :
    RecyclerView.Adapter<StarredNoticesListItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val textViewName : TextView = view.findViewById(R.id.textViewNameStarredNoticesListItem)
        val textViewBirthdate: TextView = view.findViewById(R.id.textViewBirthdateStarredNoticesListItem)
        val iconImage: ImageView = view.findViewById(R.id.iconStarredNoticesListItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.layout_starred_notices_list, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: Notice = dataset[position]
        Log.i("StarredNoticesList", "loadingStarredNotice: $item")
        holder.textViewName.text = "${item.firstName} ${item.lastName}"
        holder.textViewBirthdate.text =  if (Util.isBlankStr(item.birthDate)) "" else item.birthDate
        holder.iconImage.load(SDO.getImage(context, item)){
            placeholder(android.R.drawable.stat_sys_download)
           error(com.google.android.material.R.drawable.mtrl_ic_error)
        }
        holder.textViewName.setOnClickListener{
            val bottomSheetDialog = NoticeInfoFragment()
            val a : AppCompatActivity = it.context as AppCompatActivity
            val args = Bundle()
            args.putString("notice", item.id)
            bottomSheetDialog.arguments = args
            a.supportFragmentManager.beginTransaction().add(bottomSheetDialog, "").commit()
        }
        holder.iconImage.setOnClickListener{
            val imgView = ImageView(it.context)
            imgView.load(SDO.getImage(context, item)){
                placeholder(android.R.drawable.stat_sys_download)
                error(com.google.android.material.R.drawable.mtrl_ic_error)
            }
            imgView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            imgView.adjustViewBounds = true

            AlertDialog.Builder(it.context).setPositiveButton("ok"){dialogInter, _ -> dialogInter.dismiss()}.setView(imgView).show()
        }
    }

    fun updateData(newData : List<Notice>){
        dataset = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
