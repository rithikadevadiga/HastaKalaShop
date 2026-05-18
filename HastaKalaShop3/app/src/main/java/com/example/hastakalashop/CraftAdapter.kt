package com.example.hastakalashop

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hastakalashop.data.Craft

class CraftAdapter(
    private var crafts: List<Craft>,
    private val onDeleteClick: (Craft) -> Unit
) : RecyclerView.Adapter<CraftAdapter.CraftViewHolder>() {

    class CraftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivCraftItemImage)
        val tvName: TextView = itemView.findViewById(R.id.tvCraftItemName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCraftItemCategory)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCraftItemPrice)
        val tvNotes: TextView = itemView.findViewById(R.id.tvCraftItemNotes)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteCraft)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CraftViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_craft, parent, false)
        return CraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: CraftViewHolder, position: Int) {
        val craft = crafts[position]
        holder.tvName.text = craft.name
        holder.tvCategory.text = craft.category
        holder.tvPrice.text = "Price: ₹${craft.price}"
        holder.tvNotes.text = craft.notes
        
        if (craft.imageUri.isNotEmpty()) {
            try {
                holder.ivImage.setImageURI(Uri.parse(craft.imageUri))
            } catch (e: Exception) {
                holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(craft)
        }
    }

    override fun getItemCount() = crafts.size

    fun updateList(newList: List<Craft>) {
        this.crafts = newList
        notifyDataSetChanged()
    }
}
