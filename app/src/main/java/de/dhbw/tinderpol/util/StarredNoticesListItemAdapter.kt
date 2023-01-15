package de.dhbw.tinderpol.util

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.dhbw.tinderpol.NoticeInfoFragment
import de.dhbw.tinderpol.R
import de.dhbw.tinderpol.SDO
import de.dhbw.tinderpol.data.Notice

class StarredNoticesListItemAdapter (private val context : Context, private val dataset: List<Notice>) :
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
        val item = dataset[position]
        holder.textViewName.text = "${item.firstName} ${item.lastName}"
        holder.textViewBirthdate.text =  if (Util.isBlankStr(item.birthDate)) "" else item.birthDate
        holder.iconImage.load(SDO.getImageURL(item)){
            placeholder(android.R.drawable.stat_sys_download)
           error(com.google.android.material.R.drawable.mtrl_ic_error)
        }
        holder.iconImage.setOnClickListener{
            val bottomSheetDialog = NoticeInfoFragment()
            val a : AppCompatActivity = it.context as AppCompatActivity
            val args = Bundle()
            args.putString("notice", item.id)
            bottomSheetDialog.arguments = args
            a.supportFragmentManager.beginTransaction().add(bottomSheetDialog, "").commit()
            Toast.makeText(context, "You selected: ${item.firstName}. Imagine an info fragment popping up. Thx", Toast.LENGTH_SHORT).show()

        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
