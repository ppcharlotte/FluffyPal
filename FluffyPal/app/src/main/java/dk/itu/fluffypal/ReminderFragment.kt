package dk.itu.fluffypal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ReminderFragment : Fragment() {
    private val viewModel: ReminderViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var adapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.reminderRecyclerView)
        emptyView = view.findViewById(R.id.emptyReminder)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ReminderAdapter(listOf())
        recyclerView.adapter = adapter

        viewModel.reminderList.observe(viewLifecycleOwner) { reminders ->
            adapter.updateList(reminders)

            if (reminders.isNullOrEmpty()) {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.loadReminders()

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val reminder = adapter.getItem(position)
                viewModel.deleteReminder(reminder.id)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}