package dk.itu.fluffypal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddOptionFragment: BottomSheetDialogFragment() {
    private val petListViewModel: PetListViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentFragment =  findNavController().currentDestination?.id

        view.findViewById<LinearLayout>(R.id.add_more_pet).setOnClickListener {
            if (currentFragment == R.id.HomeFragment) {
                findNavController().navigate(R.id.action_home_to_petAdd1)
            } else {
                Toast.makeText(requireContext(), "Pets can only be added from the homepage", Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.add_record).setOnClickListener {
            view.post {
                petListViewModel.loadPets()
                AddRecordFragment().show(requireActivity().supportFragmentManager, "AddRecord")
            }
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.add_reminder).setOnClickListener {
            view.post {
                petListViewModel.loadPets()
                AddReminderFragment().show(requireActivity().supportFragmentManager, "AddReminder")
            }
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.add_weight).setOnClickListener {
            view.post {
                petListViewModel.loadPets()
                AddWeightFragment().show(requireActivity().supportFragmentManager, "AddWeight")
            }
            dismiss()
        }
    }
}
