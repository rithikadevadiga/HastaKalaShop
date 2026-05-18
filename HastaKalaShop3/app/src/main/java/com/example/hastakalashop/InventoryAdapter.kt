package com.example.hastakalashop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hastakalashop.data.InventoryItem

class InventoryAdapter(private var items: List<InventoryItem>) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    // ViewHolder holds the UI elements for a single inventory card
    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvInvItemName)
        val tvColor: TextView = itemView.findViewById(R.id.tvInvItemColor)
        val tvAdded: TextView = itemView.findViewById(R.id.tvStockAdded)
        val tvSold: TextView = itemView.findViewById(R.id.tvStockSold)
        val tvRemaining: TextView = itemView.findViewById(R.id.tvRemainingStock)
        val tvWarning: TextView = itemView.findViewById(R.id.tvLowStockWarning)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        // Inflate the item_inventory.xml layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        
        // Set basic product info
        holder.tvName.text = item.productName
        holder.tvColor.text = context.getString(R.string.color_label, item.color)
        
        // Display stock numbers
        holder.tvAdded.text = item.stockAdded.toString()
        holder.tvSold.text = item.stockSold.toString()
        holder.tvRemaining.text = item.remainingStock.toString()

        // --- Low Stock Warning Logic ---
        // If remaining stock is less than 2 (i.e., 0 or 1), show the alert
        if (item.remainingStock < 2) {
            holder.tvWarning.visibility = View.VISIBLE
        } else {
            // Hide the alert if stock is 2 or more
            holder.tvWarning.visibility = View.GONE
        }
    }

    override fun getItemCount() = items.size

    // Helper function to update the list when data changes
    fun updateData(newItems: List<InventoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
