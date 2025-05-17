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


class RecordFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyRecord: View
    private val recordViewModel: RecordViewModel by activityViewModels()
    private lateinit var adapter: RecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyRecord = view.findViewById(R.id.emptyRecord)
        recyclerView = view.findViewById(R.id.recordRecyclerView)

        adapter = RecordAdapter(listOf())
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recordViewModel.recordList.observe(viewLifecycleOwner) { records ->
            adapter.updateList(records)

            if (records.isNullOrEmpty()) {
                emptyRecord.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyRecord.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
        recordViewModel.loadAllRecords()

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val record = adapter.getItem(position)
                recordViewModel.deleteRecord(record.id)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}