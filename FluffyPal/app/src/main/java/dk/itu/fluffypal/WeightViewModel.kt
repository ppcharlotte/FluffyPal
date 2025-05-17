package dk.itu.fluffypal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dk.itu.fluffypal.database.PetDBHelper


class WeightViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PetDBHelper.getInstance(application.applicationContext)

    private val sWeights = MutableLiveData<List<PetWeight>>()
    private val sSelectedPetId = MutableLiveData<Int>()
    val selectedPetId: LiveData<Int> get() = sSelectedPetId
    val weights: LiveData<List<PetWeight>> = sWeights


    var lastAddedPetId: Int? = null

    fun loadWeights(petId: Int) {
        val list = db.getWeightsForPet(petId)
        sWeights.value = list

        list.maxByOrNull { it.date }?.let {
            db.updatePetWeight(petId, "${it.weight} ")
        }
    }

    fun addWeight(weight: PetWeight) {
        db.insertWeight(weight)
        lastAddedPetId = weight.petId
        loadWeights(weight.petId)
    }

    fun selectPet(petId: Int) {
        sSelectedPetId.value = petId
        loadWeights(petId)
    }

    fun onPetDeleted(petId: Int) {
        if (sSelectedPetId.value == petId) {
            val remainingPets = PetDBHelper.getInstance(getApplication()).PetList().filter { it.id != petId }
            val nextPet = remainingPets.firstOrNull()
            if (nextPet != null) {
                selectPet(nextPet.id)
            } else {
                sWeights.value = emptyList()
            }
        }
    }

}