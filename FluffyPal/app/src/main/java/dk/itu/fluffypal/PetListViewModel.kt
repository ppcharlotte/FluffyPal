package dk.itu.fluffypal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dk.itu.fluffypal.database.PetDBHelper


class PetListViewModel(application: Application) : AndroidViewModel(application)  {
    private val db = PetDBHelper.getInstance(getApplication<Application>().applicationContext)
    private val petListT = MutableLiveData<List<PetCard>>()
    private val deleteE = MutableLiveData<String?>()
    val petList: LiveData<List<PetCard>> = petListT
    val deleteEvent: LiveData<String?> get() = deleteE

    var selectedPet:String? = null
    var petName: String? = null
    var petBirthday: String? = null
    var petArrival: String? = null
    var petImageUri: String? = null
    var petGender: String? = null
    var petNeutered: String? = null
    var petWeight: String? = null

    fun loadPets(){
        val pets = db.PetList()
        petListT.value = pets.map{ it.toCard()}
    }

    fun deletePet(petName: String){
        val selected = db.PetList().find{ it.name == petName}
        selected?.let{
            db.deletePetAndAllRelatedData(it.id)
            deleteE.value = "${it.name} deleted"
            loadPets()
        }
    }

    fun clearDeleteEvent() {
        deleteE.value = null
    }

    fun getPetIdByName(name: String): Int? {
        return db.PetList().find { it.name == name }?.id
    }

    fun clearPetData() {
        selectedPet = null
        petName = null
        petBirthday = null
        petArrival = null
        petImageUri = null
        petGender = null
        petNeutered = null
        petWeight = null
    }

    fun updatePetWeightToLatest(petId: Int) {
        val currentWeights = PetDBHelper.getInstance(getApplication()).getWeightsForPet(petId)

        val latest = currentWeights.sortedWith(
            compareByDescending<PetWeight> { it.date }.thenByDescending { it.id }
        ).firstOrNull()

        latest?.let {
            PetDBHelper.getInstance(getApplication()).updatePetWeight(petId, it.weight)
            loadPets()
        }
    }
}
