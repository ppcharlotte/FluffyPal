package dk.itu.fluffypal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dk.itu.fluffypal.database.PetDBHelper

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PetDBHelper.getInstance(getApplication())
    private val sReminderList = MutableLiveData<List<PetReminder>>()
    val reminderList: LiveData<List<PetReminder>> get() = sReminderList
    var selectedPetId: Int? = null

    fun loadReminders() {
        sReminderList.value = db.getReminders()
    }

    fun addReminder(reminder: PetReminder) {
        db.insertReminder(reminder)
        loadReminders()
    }

    fun deleteReminder(id: Int) {
        db.deleteReminder(id)
        loadReminders()
    }
}