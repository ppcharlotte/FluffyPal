package dk.itu.fluffypal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dk.itu.fluffypal.database.PetDBHelper

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PetDBHelper.getInstance(application.applicationContext)

    private val sRecordList = MutableLiveData<List<PetRecord>>()
    val recordList: LiveData<List<PetRecord>> = sRecordList
    var selectedPetId: Int? = null

    private val sRecordAdded = MutableLiveData<Boolean>()

    fun loadAllRecords() {
        sRecordList.value = db.getAllRecords()
    }

    fun addRecord(record: PetRecord) {
        db.insertRecord(record)
        sRecordAdded.value = true
        loadAllRecords()
    }

    fun deleteRecord(id: Int) {
        db.deleteRecord(id)
        loadAllRecords()
    }
}