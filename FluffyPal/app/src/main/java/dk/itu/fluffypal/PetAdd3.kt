package dk.itu.fluffypal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dk.itu.fluffypal.database.PetDBHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PetAdd3 : Fragment() {
    private var sGender: String? = null
    private var sNeutered: String? =null
    private val petViewModel: PetListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_pet__add3, container, false)
        val genderGroup = v.findViewById<RadioGroup>(R.id.gender_group)
        val neuteredGroup=v.findViewById<RadioGroup>(R.id.neutered_group)
        val nextAdd3 = v.findViewById<Button>(R.id.add_pet_next3)
        val backAdd3 = v.findViewById<Button>(R.id.add_pet_back3)
        val weightInput = v.findViewById<EditText>(R.id.what_weight)


        genderGroup.setOnCheckedChangeListener{_, checkId->
            sGender = when(checkId){
                R.id.gender_girl -> "Girl"
                R.id.gender_boy -> "Boy"
                R.id.gender_others -> "Other"
                else ->null
            }
        }

        neuteredGroup.setOnCheckedChangeListener{_, checkId->
            sNeutered = when(checkId){
                R.id.neutered_yes -> "Yes"
                R.id.neutered_no -> "No"
                R.id.neutered_others -> "Not Sure"
                else ->null
            }
        }

        nextAdd3.setOnClickListener {
            val weightT = weightInput.text.toString().trim()
            val weight = if (weightT.isEmpty()) {
                "Unknown"
            } else {
                val number = weightT.toFloatOrNull()
                if (number == null) {
                    Toast.makeText(
                        requireContext(),
                        "Please enter a valid number for weight",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                weightT
            }

            if (sGender == null || sNeutered == null) {
                Toast.makeText(
                    requireContext(),
                    "Please select gender and neutered status",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            petViewModel.petGender = sGender
            petViewModel.petNeutered = sNeutered
            petViewModel.petWeight = weight

            if (petViewModel.petImageUri.isNullOrEmpty()) {
                petViewModel.petImageUri = getDefaultImageUriForCategory(petViewModel.selectedPet)
            }


            val pet = Pet(
                category = petViewModel.selectedPet ?: "",
                name = petViewModel.petName ?: "",
                birthday = petViewModel.petBirthday ?: "",
                arrivalDate = petViewModel.petArrival ?: "",
                gender = petViewModel.petGender ?: "",
                neutered = petViewModel.petNeutered ?: "",
                weight = petViewModel.petWeight ?: "",
                imageuri = petViewModel.petImageUri ?: ""
            )

            findNavController().popBackStack(R.id.HomeFragment, false)

            val db = PetDBHelper.getInstance(requireContext())
            db.insertPet(pet)

            val petId = db.PetList().find { it.name == pet.name }?.id
            if (petId != null && pet.weight != "Unknown") {
                val entry = PetWeight(
                    id = 0,
                    petId = petId,
                    weight = pet.weight,
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
                db.insertWeight(entry)
                petViewModel.clearPetData()
            }
        }


        backAdd3.setOnClickListener {
            findNavController().navigateUp()
        }

        return v
    }
}

fun getDefaultImageUriForCategory(category: String?): String {
    return when (category) {
        "dog" -> "android.resource://dk.itu.fluffypal/drawable/dog"
        "cat" -> "android.resource://dk.itu.fluffypal/drawable/catcat"
        "rabbit" -> "android.resource://dk.itu.fluffypal/drawable/rabbit"
        "hamster" -> "android.resource://dk.itu.fluffypal/drawable/hamster"
        "bird" -> "android.resource://dk.itu.fluffypal/drawable/bird"
        "fish" -> "android.resource://dk.itu.fluffypal/drawable/fish"
        "Others" -> "android.resource://dk.itu.fluffypal/drawable/fluffy"
        else -> "android.resource://dk.itu.fluffypal/drawable/placeholder"
    }
}
