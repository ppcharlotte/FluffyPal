package dk.itu.fluffypal

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.fluffypal.database.PetDBHelper

class RecordAdapter(
    private var records: List<PetRecord>
) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petImage: ImageView = view.findViewById(R.id.pet_image)
        val activityText: TextView = view.findViewById(R.id.record_activity)
        val recordDate: TextView = view.findViewById(R.id.record_date)
        val description: TextView = view.findViewById(R.id.record_description)
        val recordImage: ImageView = view.findViewById(R.id.record_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pet_recordlist, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.activityText.text = record.title
        holder.recordDate.text = record.date
        holder.description.text = record.description

        val context = holder.itemView.context
        holder.recordImage.setImageURI(record.recordImageUri?.let { Uri.parse(it) })
        if (record.recordImageUri.isNullOrBlank()) {
            holder.recordImage.visibility = View.GONE
        } else {
            holder.recordImage.visibility = View.VISIBLE
            holder.recordImage.setImageURI(Uri.parse(record.recordImageUri))
        }

        val pet = PetDBHelper.getInstance(context).PetList().find { it.id == record.petId }
        holder.petImage.setImageURI(pet?.imageuri?.let { Uri.parse(it) })
    }

    override fun getItemCount(): Int = records.size

    fun updateList(newList: List<PetRecord>){
        records = newList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): PetRecord = records[position]
}