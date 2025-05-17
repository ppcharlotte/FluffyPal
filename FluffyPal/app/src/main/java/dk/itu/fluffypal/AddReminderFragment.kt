package dk.itu.fluffypal

import android.app.DatePickerDialog
import android.content.DialogInterface
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

class AddReminderFragment : BottomSheetDialogFragment() {

    private val reminderViewModel: ReminderViewModel by activityViewModels()
    private val petListViewModel: PetListViewModel by activityViewModels()
    private var selectedPetId: Int? = null
    private var reminderDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_reminder, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val petRadioGroup = view.findViewById<RadioGroup>(R.id.petRadioGroupR)
        val titleInput = view.findViewById<EditText>(R.id.reminderTitleInput)
        val notesInput = view.findViewById<EditText>(R.id.reminderNotes)
        val dateInput = view.findViewById<EditText>(R.id.what_reminder_date)
        val saveButton = view.findViewById<Button>(R.id.saveReminderButton)
        val petList = petListViewModel.petList.value

        reminderDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dateInput.setText(reminderDate)
        dateInput.setOnClickListener {
            val cal = Calendar.getInstance()
            val datePicker =DatePickerDialog(requireContext(), { _, y, m, d ->
                reminderDate = "%04d-%02d-%02d".format(y, m + 1, d)
                dateInput.setText(reminderDate)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            datePicker.datePicker.minDate = cal.timeInMillis
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
                    reminderViewModel.selectedPetId = selectedPetId
                }
            }
            petRadioGroup.addView(radioButton)
        }

        reminderViewModel.selectedPetId?.let { id ->
            selectedPetId = id
            petRadioGroup.forEach { radioButton ->
                if (radioButton.tag == id) {
                    (radioButton as RadioButton).isChecked = true
                }
            }
        }

        saveButton.setOnClickListener {
            val title = titleInput.text.toString()
            val notes = notesInput.text.toString()
            if (title.isNotBlank() && selectedPetId != null) {
                val reminder = PetReminder(0, selectedPetId!!, title, notes, reminderDate!!)
                reminderViewModel.addReminder(reminder)
                dismiss()
                val navView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
                navView.selectedItemId = R.id.ReminderFragment
            } else {
                Toast.makeText(requireContext(), "Please fill in required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
            reminderViewModel.selectedPetId = null
    }
}
