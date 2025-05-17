package dk.itu.fluffypal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class PetAdd1Fragment : Fragment() {

    private var petSelected: String? = null
    private lateinit var petImages: List<ImageView>
    private val petViewModel: PetListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pet__add1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        petImages = listOf(
            view.findViewById(R.id.image_dog),
            view.findViewById(R.id.image_cat),
            view.findViewById(R.id.image_rabbit),
            view.findViewById(R.id.image_hamster),
            view.findViewById(R.id.image_bird),
            view.findViewById(R.id.image_fish),
            view.findViewById(R.id.image_fluffy)
        )

        petImages.forEach { imageView ->
            imageView.setOnClickListener {
                petImages.forEach { it.setBackgroundResource(0) }
                imageView.setBackgroundResource(R.drawable.selected_border)
                petSelected = imageView.tag as? String
                petViewModel.selectedPet = petSelected
            }
        }

        petViewModel.selectedPet?.let { selectedPet ->
            petImages.forEach { imageView ->
                if (imageView.tag == selectedPet) {
                    imageView.setBackgroundResource(R.drawable.selected_border)
                    petSelected = selectedPet
                }
            }
        }

        view.findViewById<Button>(R.id.add_pet_next1).setOnClickListener{
            if(petSelected!=null){
                petViewModel.selectedPet = petSelected
                findNavController().navigate(R.id.action_petAdd1_to_petAdd2)
            } else{
                Toast.makeText(requireContext(), "Please choose a category", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.add_pet_back1).setOnClickListener {
            findNavController().navigateUp()
            petViewModel.selectedPet = null
            petViewModel.petImageUri = null
        }
    }
}
