package dk.itu.fluffypal

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.fluffypal.database.PetDBHelper

class ReminderAdapter(private var reminders: List<PetReminder>) :
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    class ReminderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petImage: ImageView = view.findViewById(R.id.pet_image_reminder)
        val title: TextView = view.findViewById(R.id.reminder_activity)
        val date: TextView = view.findViewById(R.id.planned_date)
        val notes: TextView = view.findViewById(R.id.reminder_notes)
        val countdown: TextView = view.findViewById(R.id.count_down)
    }

    fun updateList(newList: List<PetReminder>) {
        reminders = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pet_reminderlist, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.title.text = reminder.title
        holder.date.text = reminder.date
        holder.notes.text = reminder.notes ?: ""

        val daysLeft = calculateDaysLeft(reminder.date)
        holder.countdown.text = "$daysLeft days left"

        val context = holder.itemView.context
        val pet = PetDBHelper.getInstance(context).PetList().find { it.id == reminder.petId }
        holder.petImage.setImageURI(pet?.imageuri?.let { Uri.parse(it) })

        if (reminder.notes.isNullOrBlank()) {
            holder.notes.visibility = View.GONE
        } else {
            holder.notes.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = reminders.size

    fun getItem(position: Int): PetReminder = reminders[position]

}