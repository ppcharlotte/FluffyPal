package dk.itu.fluffypal

import android.icu.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar


data class Pet(
    val id: Int = 0,
    val category: String,
    val name: String,
    val birthday: String,
    val arrivalDate: String,
    val gender: String,
    val neutered: String,
    val weight: String,
    val imageuri: String
)

data class PetCard(
    val id: Int = 0,
    val petName: String,
    val petBirthday: String,
    val petGender: String,
    val petWeight: String,
    val petImage: String,
    val petAge: String,
    val petTimeTogether: String
)

data class PetRecord(
    val id: Int,
    val petId: Int,
    val title: String,
    val date: String,
    val description: String,
    val recordImageUri:String?
)

data class PetReminder(
    val id: Int,
    val petId: Int,
    val title: String,
    val notes: String?,
    val date: String
)

data class PetWeight(
    val id: Int = 0,
    val petId: Int,
    val weight: String,
    val date: String
)

fun Pet.toCard(): PetCard {
    return PetCard(
        id = id,
        petName = name,
        petBirthday = birthday,
        petGender = gender,
        petWeight = "$weight kg",
        petImage = imageuri,
        petAge = calculateAge(birthday),
        petTimeTogether = calculateTimeTogether(arrivalDate)
    )
}

fun calculateAge(birthday: String): String{
    return try{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = sdf.parse(birthday)
        val today = Calendar.getInstance().time

        val diff = today.time - birthDate.time
        val diffInYears = diff / (1000L*60*60*24*365)
        val remaining = diff % (1000L * 60 * 60 * 24 * 365)
        val diffInMonths = remaining / (1000L*60*60*24*30)

        "$diffInYears yrs $diffInMonths mos"
    } catch (e: Exception) {
        "Unknown"
    }
}

fun calculateTimeTogether(arrival: String): String{
    return try{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val arrivalDate1 = sdf.parse(arrival)
        val today = Calendar.getInstance().time

        val diff = today.time - arrivalDate1.time
        val diffInDays = diff / (1000L*60*60*24)

        "$diffInDays days"
    } catch (e: Exception) {
        "Unknown"
    }
}

fun calculateDaysLeft(dateString: String): Long {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val reminderDate = sdf.parse(dateString)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val diff = reminderDate.time - today.time
        maxOf(diff / (1000 * 60 * 60 * 24), 0)
    } catch (e: Exception) {
        0
    }
}



