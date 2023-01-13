package de.dhbw.tinderpol.util

import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import de.dhbw.tinderpol.R
import de.dhbw.tinderpol.data.Notice

class StarredNoticesListItemAdapter (private val context : Context, private val dataset: List<Notice>) :
    RecyclerView.Adapter<StarredNoticesListItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val textView : TextView = view.findViewById(R.id.textViewStarredNoticesListItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.layout_starred_notices_list, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.textView.text = "${item.firstName} ${item.lastName}"
        holder.textView.setOnClickListener{
            Toast.makeText(context, "You selected: ${item.firstName}. Imagine an info fragment popping up. Thx", Toast.LENGTH_SHORT).show()

        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}