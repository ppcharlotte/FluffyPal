package dk.itu.fluffypal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyLayout: View
    private val viewModel: PetListViewModel by viewModels()
    private lateinit var adapter: PetAdapter
    private val weightViewModel: WeightViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyLayout = view.findViewById(R.id.empty_layout)
        recyclerView = view.findViewById(R.id.petRecyclerView)


        adapter = PetAdapter(listOf()) { }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.add_pet).setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToPetAdd1()
            findNavController().navigate(action)
        }

        viewModel.petList.observe(viewLifecycleOwner) { pets ->
            adapter.updateList(pets)

            if (pets.isEmpty()) {
                emptyLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val petToDelete = adapter.getItem(position)
                viewModel.getPetIdByName(petToDelete.petName)?.let { petId ->
                    weightViewModel.onPetDeleted(petId)
                    viewModel.deletePet(petToDelete.petName)}
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        viewModel.deleteEvent.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearDeleteEvent()
            }
        }
        viewModel.loadPets()

    }
}