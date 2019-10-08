package fr.epita.android.hellogames.Adapters

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epita.android.hellogames.Models.GameObject
import fr.epita.android.hellogames.R

class GameListAdapter(
    private val context : Context,
    private val data: MutableList<GameObject>,
    private val itemOnClickListener: View.OnClickListener
) : RecyclerView.Adapter<GameListAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.gameObjectName)
        val pictureView: ImageView = itemView.findViewById(R.id.gameObjectPicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView = LayoutInflater
            .from(context)
            .inflate(R.layout.activity_list_view_row, parent, false)

        rowView.setOnClickListener(itemOnClickListener)

        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = data[position]

        holder.nameView.text = currentItem.name
        Glide
            .with(context)
            .load(currentItem.picture)
            .error(getDrawable(context, R.drawable.error_loading))
            .into(holder.pictureView)

        holder.itemView.tag = currentItem.id
    }
}