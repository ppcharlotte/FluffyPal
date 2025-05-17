package dk.itu.fluffypal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeightAdapter(private var weights: List<PetWeight>) :
    RecyclerView.Adapter<WeightAdapter.WeightViewHolder>() {

    class WeightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val weightText: TextView = view.findViewById(R.id.weight)
        val dateText: TextView = view.findViewById(R.id.weight_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pet_weightlist, parent, false)
        return WeightViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val weight = weights[position]
        holder.weightText.text = weight.weight + "kg"
        holder.dateText.text = weight.date
    }

    override fun getItemCount(): Int = weights.size

    fun updateList(newList: List<PetWeight>) {
        weights = newList
        notifyDataSetChanged()
    }
}