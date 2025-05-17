package dk.itu.fluffypal

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import dk.itu.fluffypal.database.PetDBHelper

class ManagementFragment : Fragment() {

    private lateinit var choosePetLayout: LinearLayout
    private lateinit var latestWeightText: TextView
    private lateinit var latestWeightDate: TextView
    private lateinit var weightChangeText: TextView
    private lateinit var lastWeightDayText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var dataView: View
    private lateinit var adapter: WeightAdapter


    private lateinit var petList: List<Pet>

    private val weightViewModel: WeightViewModel by activityViewModels{
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }

    private var lastSelectedIcon: ShapeableImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        choosePetLayout = view.findViewById(R.id.choose_pet)
        latestWeightText = view.findViewById(R.id.latestWeight)
        latestWeightDate = view.findViewById(R.id.latest_record_date)
        weightChangeText = view.findViewById(R.id.weight_change)
        lastWeightDayText = view.findViewById(R.id.last_weightDay)
        recyclerView = view.findViewById(R.id.weightRecordList)
        emptyView = view.findViewById(R.id.emptyManagement)
        dataView = view.findViewById(R.id.with_weight_data)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WeightAdapter(listOf())
        recyclerView.adapter = adapter

        petList = PetDBHelper.getInstance(requireContext()).PetList()
        if (petList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            dataView.visibility = View.GONE
            return
        } else {
            emptyView.visibility = View.GONE
            dataView.visibility = View.VISIBLE
        }

        if (weightViewModel.selectedPetId.value == null) {
            weightViewModel.selectPet(petList.first().id)
        }

        observeWeightsFor()
    }

    private fun observeWeightsFor() {
        weightViewModel.selectedPetId.observe(viewLifecycleOwner) { petId ->
            buildPetSelector(petId)
        }

        weightViewModel.weights.observe(viewLifecycleOwner) { weights ->
            if (weights.isNullOrEmpty()) {
                latestWeightText.text = "—"
                latestWeightDate.text = "—"
                weightChangeText.text = "—"
                lastWeightDayText.text = "—"
                adapter.updateList(emptyList())
            } else {
                val sorted = weights.sortedWith(compareByDescending<PetWeight> { it.date }.thenByDescending { it.id })
                val latest = sorted.firstOrNull()
                val prev = if (sorted.size > 1) sorted[1] else null

                latestWeightText.text = "${latest?.weight ?: "-"}kg"
                latestWeightDate.text = latest?.date ?: "-"
                weightChangeText.text = if (prev != null) {
                    val diff = latest!!.weight.toFloat() - prev.weight.toFloat()
                    if (diff >= 0) "+${diff}kg" else "${diff}kg"
                } else "—"
                lastWeightDayText.text = prev?.date ?: "—"
                adapter.updateList(sorted)
            }
        }
    }

    private fun buildPetSelector(highlightId: Int?) {
        choosePetLayout.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        for (pet in petList) {
            val petView = inflater.inflate(R.layout.pet_selection, choosePetLayout, false)
            val petIcon = petView.findViewById<ShapeableImageView>(R.id.pet_icon)
            val nameView = petView.findViewById<TextView>(R.id.pet_name)

            petIcon.setImageURI(Uri.parse(pet.imageuri))
            nameView.text = pet.name

            if (pet.id == highlightId) {
                petView.post {
                    lastSelectedIcon?.strokeWidth = 0f
                    petIcon.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.purple)
                    )
                    petIcon.strokeWidth = 4f
                    lastSelectedIcon = petIcon
                }
            }

            petView.setOnClickListener {
                weightViewModel.selectPet(pet.id)
            }

            choosePetLayout.addView(petView)
        }
    }
}