package dk.itu.fluffypal

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import java.io.File
import java.io.FileOutputStream

class PetAdd2 : Fragment() {

    private val petViewModel: PetListViewModel by activityViewModels()
    private val birthdayCalendar = Calendar.getInstance()
    private val arrivalCalendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var sImageLauncher: ActivityResultLauncher<Intent>
    private var sImageUri: Uri?=null
    
    companion object {
        private const val MAX_IMAGE_DIMENSION = 1024
        private const val COMPRESSION_QUALITY = 80
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pet__add2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val petName = view.findViewById<EditText>(R.id.what_name)
        val petBirthday = view.findViewById<EditText>(R.id.what_birthday)
        val petArrival = view.findViewById<EditText>(R.id.what_arrival)
        val nextAdd2 = view.findViewById<Button>(R.id.add_pet_next2)
        val petImage = view.findViewById<ImageView>(R.id.animal_img)

        val birthdayDatePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            birthdayCalendar.set(Calendar.YEAR, year)
            birthdayCalendar.set(Calendar.MONTH, month)
            birthdayCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateBirthdayLabel(petBirthday)
        }

        // Setup date picker for arrival date
        val arrivalDatePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            arrivalCalendar.set(Calendar.YEAR, year)
            arrivalCalendar.set(Calendar.MONTH, month)
            arrivalCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateArrivalLabel(petArrival)
        }

        // Show date picker when birthday field is clicked
        petBirthday.setOnClickListener {
            context?.let { ctx ->
                val datePickerDialog = DatePickerDialog(
                    ctx,
                    birthdayDatePicker,
                    birthdayCalendar.get(Calendar.YEAR),
                    birthdayCalendar.get(Calendar.MONTH),
                    birthdayCalendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
                datePickerDialog.show()
            }
        }

        petArrival.setOnClickListener {
            context?.let { ctx ->
                val datePickerDialog = DatePickerDialog(
                    ctx,
                    arrivalDatePicker,
                    arrivalCalendar.get(Calendar.YEAR),
                    arrivalCalendar.get(Calendar.MONTH),
                    arrivalCalendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.datePicker.minDate = birthdayCalendar.timeInMillis
                datePickerDialog.show()
            }
        }

        view.findViewById<Button>(R.id.add_pet_back2).setOnClickListener {
            findNavController().navigateUp()
        }

        sImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    val fileName = "avatar_${System.currentTimeMillis()}.jpg"

                    val localPath = copyImageToInternalStorage(uri, fileName)

                    if (localPath != null) {
                        sImageUri = Uri.fromFile(File(localPath))
                        petImage.setImageURI(sImageUri)
                        petViewModel.petImageUri = sImageUri.toString()
                    } else {
                        Toast.makeText(requireContext(), "Can' safe image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        petViewModel.petImageUri?.let { uriString ->
            val uri = Uri.parse(uriString)
            petImage.setImageURI(uri)
            sImageUri = uri
        }

        petImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            sImageLauncher.launch(intent)
        }


        nextAdd2.setOnClickListener {
            val name = petName.text.toString()
            val birthday = petBirthday.text.toString()
            val arrival = petArrival.text.toString()
            val imageUri = sImageUri?.toString()?:""

            if (petName.text.isNotEmpty() && birthday.isNotEmpty() && arrival.isNotEmpty()) {
                petViewModel.petName = name
                petViewModel.petBirthday = birthday
                petViewModel.petArrival = arrival
                petViewModel.petImageUri = imageUri

                findNavController().navigate(R.id.action_petAdd2_to_petAdd3)
            } else{
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateBirthdayLabel(editText: EditText) {
        editText.setText(dateFormat.format(birthdayCalendar.time))
    }


    private fun updateArrivalLabel(editText: EditText) {
        editText.setText(dateFormat.format(arrivalCalendar.time))
    }

    private fun refactorImage(uri: Uri): Bitmap? {
        val context = requireContext()
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }

            var scale = 1
            while (options.outWidth / scale > MAX_IMAGE_DIMENSION || 
                   options.outHeight / scale > MAX_IMAGE_DIMENSION) {
                scale *= 2
            }

            val finalOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }
            
            return context.contentResolver.openInputStream(uri)?.use { 
                BitmapFactory.decodeStream(it, null, finalOptions)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun copyImageToInternalStorage(uri: Uri, fileName: String): String? {
        return try {
            val bitmap = refactorImage(uri) ?: return null
            val file = File(requireContext().filesDir, fileName)
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, fos)
            }
            bitmap.recycle()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}