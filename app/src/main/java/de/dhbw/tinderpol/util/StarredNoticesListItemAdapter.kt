package de.dhbw.tinderpol.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.dhbw.tinderpol.NoticeInfoFragment
import de.dhbw.tinderpol.R
import de.dhbw.tinderpol.SDO
import de.dhbw.tinderpol.data.Notice
import java.io.File


class StarredNoticesListItemAdapter (private val context : Context, private var dataset: List<Notice>) :
    RecyclerView.Adapter<StarredNoticesListItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val textViewName : TextView = view.findViewById(R.id.textViewNameStarredNoticesListItem)
        val textViewBirthdate: TextView = view.findViewById(R.id.textViewBirthdateStarredNoticesListItem)
        val iconImage: ImageView = view.findViewById(R.id.iconStarredNoticesListItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        Log.i("starredNoticesList", "creating view holder")
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.layout_starred_notices_list, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @SuppressLint("PrivateResource")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: Notice = dataset[position]
        Log.i("starredNoticesList", "loadingStarredNotice: $item")
        holder.textViewName.text = "${item.firstName} ${item.lastName}"
        holder.textViewBirthdate.text =  if (Util.isBlankStr(item.birthDate)) "" else item.birthDate.toString()
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
            var imgNo = 0
            val imgLength = item.imgs?.size ?: 0
            imgView.load(SDO.getImage(context, item, imgNo)){
                placeholder(android.R.drawable.stat_sys_download)
                error(Drawable.createFromPath(File(context.getDir(
                        "images", Context.MODE_PRIVATE),
                        SDO.EMPTY_NOTICE_ID + "_0"
                    ).absolutePath
                ))
            }
            imgView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            imgView.adjustViewBounds = true

            val alert = AlertDialog.Builder(it.context).setPositiveButton("ok"){
                    dialogInter, _ -> dialogInter.dismiss()}.setView(imgView)

            if(imgLength > 1){
                alert.setNeutralButton("Next Image", null)
                val alertDialog: AlertDialog = alert.create()
                alertDialog.setOnShowListener { dialog ->
                    val button: Button =
                        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEUTRAL)
                    button.setOnClickListener {
                        imgNo = (imgNo + 1) % imgLength
                        imgView.load(SDO.getImage(context, item, imgNo)) {
                            placeholder(android.R.drawable.stat_sys_download)
                            error(
                                Drawable.createFromPath(
                                    File(
                                        context.getDir(
                                            "images", Context.MODE_PRIVATE
                                        ),
                                        SDO.EMPTY_NOTICE_ID + "_0"
                                    ).absolutePath
                                )
                            )
                        }
                    }
                }
                alertDialog.show()
            }
            else{
                alert.show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData : List<Notice>){
        Log.i("starredNoticesList", "updating dataset")
        dataset = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
