package dk.itu.fluffypal

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class AddWeightFragment : BottomSheetDialogFragment() {

    private val petListViewModel: PetListViewModel by activityViewModels()
    private val weightViewModel: WeightViewModel by activityViewModels()

    private var selectedPetId: Int? = null
    private var weightDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_weight, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val weightInput = view.findViewById<EditText>(R.id.input_weight)
        val dateInput = view.findViewById<EditText>(R.id.weight_date)
        val saveButton = view.findViewById<Button>(R.id.saveWeightButton)
        val petList = petListViewModel.petList.value
        val petRadioGroup = view.findViewById<RadioGroup>(R.id.petRadioGroupW)

        dateInput.setText(weightDate)
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    weightDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    dateInput.setText(weightDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = calendar.timeInMillis
            datePicker.show()
        }

        if (petList.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please add pet!", Toast.LENGTH_LONG).show()
            saveButton.isEnabled = false
            return
        }

        petListViewModel.petList.value?.forEach { pet ->
            val radioButton = RadioButton(requireContext()).apply {
                text = pet.petName
                tag = pet.id

                setOnClickListener {
                    selectedPetId = petListViewModel.getPetIdByName(pet.petName)
                    selectedPetId?.let { id ->
                        weightViewModel.selectPet(id)
                    }
                }
            }
            petRadioGroup.addView(radioButton)
        }

        weightViewModel.selectedPetId.value?.let { id ->
            selectedPetId = id
            petRadioGroup.forEach { radioButton ->
                if (radioButton.tag == id) {
                    (radioButton as RadioButton).isChecked = true
                }
            }
        }

        saveButton.setOnClickListener {
            val weightText = weightInput.text.toString()

            if (weightText.isBlank()) {
                Toast.makeText(requireContext(), "Please enter the weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val regex = Regex("^[0-9]+(\\.[0-9]+)?$")

            if (!regex.matches(weightText)) {
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedPetId == null) {
                Toast.makeText(requireContext(), "Please select a pet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val entry = PetWeight(
                id = 0,
                petId = selectedPetId!!,
                weight = weightText,
                date = weightDate
            )

            weightViewModel.addWeight(entry)
            petListViewModel.updatePetWeightToLatest(entry.petId)

            dismiss()
            val navView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
            navView.selectedItemId = R.id.ManagementFragment
        }
    }

}
