package dk.itu.fluffypal

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView


class PetAdapter(private var petList: List<PetCard>,
                 private val onItemClick: (PetCard) -> Unit) :
    RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val petImageView: ImageView = itemView.findViewById(R.id.petImageView)
        val petNameTextView: TextView = itemView.findViewById(R.id.NameTextView)
        val petGenderTextView: TextView = itemView.findViewById(R.id.GenderTextView)
        val petBirthdayTextView: TextView = itemView.findViewById(R.id.BirthdayTextView)
        val petWeightTextView: TextView = itemView.findViewById(R.id.WeightTextView)
        val petAgeTextView: TextView = itemView.findViewById(R.id.AgeTextView)
        val petArrivalTextView :TextView = itemView.findViewById(R.id.TogetherTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pet_card, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = petList[position]
        holder.petImageView.setImageURI(Uri.parse(pet.petImage))
        holder.petNameTextView.text = pet.petName
        holder.petBirthdayTextView.text = pet.petBirthday
        holder.petGenderTextView.text = pet.petGender
        holder.petWeightTextView.text = pet.petWeight
        holder.petAgeTextView.text = pet.petAge
        holder.petArrivalTextView.text = pet.petTimeTogether

        holder.itemView.setOnClickListener {
            onItemClick(pet)
        }

    }

    override fun getItemCount(): Int = petList.size

    fun updateList(newList: List<PetCard>) {
        petList = newList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): PetCard = petList[position]

}
