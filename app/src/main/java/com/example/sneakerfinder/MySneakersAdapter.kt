package com.example.sneakerfinder

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MySneakersAdapter (
    private val context: Context,
    private val catalogList: ArrayList<Any>,
    private val catalogName: ArrayList<String>,
    private val userLogged: String
) : RecyclerView.Adapter<MySneakersAdapter.MySneakersViewHolder>() {


    class MySneakersViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tv_name_catalog = v.findViewById<TextView>(R.id.tv_cat_name_sneaker)
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MySneakersViewHolder {
        return MySneakersViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_catalog,
                p0,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return catalogList.size
    }

    override fun onBindViewHolder(holder: MySneakersViewHolder, position: Int) {
        var map: Map<String, Any>
        var itemName: String = ""
        var brand: String = ""
        var model: String = ""
        var size: String = ""
        var owner: String = ""

        itemName = catalogName[position].toString()
        holder.tv_name_catalog.text = itemName

        holder.itemView.setOnClickListener {
            Toast.makeText(context, "ITEM CLICKED : " + itemName, Toast.LENGTH_SHORT).show()
            var intentToDetail = Intent(context, SneakerDetailActivity::class.java)

            map = catalogList[position] as Map<String, Any>

            brand = map["brand"].toString()
            model = map["model"].toString()
            size = map["size"].toString()
            owner = map["owner"].toString()


            intentToDetail.putExtra("brand", brand)
            intentToDetail.putExtra("model", model)
            intentToDetail.putExtra("size", size)
            intentToDetail.putExtra("owner", owner)
            intentToDetail.putExtra("userLogged", userLogged)
            intentToDetail.putExtra("add", "")
            context.startActivity(intentToDetail)

        }


    }


}