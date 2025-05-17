package dk.itu.fluffypal.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dk.itu.fluffypal.Pet
import dk.itu.fluffypal.PetRecord
import dk.itu.fluffypal.PetReminder
import dk.itu.fluffypal.PetWeight

class PetDBHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        const val DATABASE_NAME = "pets.db"
        const val DATABASE_VERSION = 1

        @Volatile private var instance: PetDBHelper? = null

        fun getInstance(context: Context): PetDBHelper {
            return instance ?: synchronized(this) {
                instance ?: PetDBHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE ${ItemsDbSchema.PetTable.NAME} (
                ${ItemsDbSchema.PetTable.Cols.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${ItemsDbSchema.PetTable.Cols.CATEGORY} TEXT,
                ${ItemsDbSchema.PetTable.Cols.NAME} TEXT,
                ${ItemsDbSchema.PetTable.Cols.BIRTHDAY} TEXT,
                ${ItemsDbSchema.PetTable.Cols.ARRIVAL} TEXT,
                ${ItemsDbSchema.PetTable.Cols.GENDER} TEXT,
                ${ItemsDbSchema.PetTable.Cols.NEUTERED} TEXT,
                ${ItemsDbSchema.PetTable.Cols.WEIGHT} TEXT,
                ${ItemsDbSchema.PetTable.Cols.IMAGE_URI} TEXT
            )
        """.trimIndent())

        db.execSQL("""
        CREATE TABLE records (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            pet_id INTEGER,
            title TEXT,
            date TEXT,
            description TEXT,
            image_uri TEXT,
            FOREIGN KEY(pet_id) REFERENCES pets(id)
        )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE reminders (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            pet_id INTEGER,
            title TEXT NOT NULL,
            notes TEXT,
            date TEXT NOT NULL,
            FOREIGN KEY(pet_id) REFERENCES pets(id)
        )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE weights (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            pet_id INTEGER,
            weight TEXT NOT NULL,
            date TEXT NOT NULL,
            FOREIGN KEY(pet_id) REFERENCES pets(id)
        )
        """.trimIndent())

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${ItemsDbSchema.PetTable.NAME}")
        onCreate(db)
    }

    fun insertPet(pet: Pet) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(ItemsDbSchema.PetTable.Cols.CATEGORY, pet.category)
                put(ItemsDbSchema.PetTable.Cols.NAME, pet.name)
                put(ItemsDbSchema.PetTable.Cols.BIRTHDAY, pet.birthday)
                put(ItemsDbSchema.PetTable.Cols.ARRIVAL, pet.arrivalDate)
                put(ItemsDbSchema.PetTable.Cols.GENDER, pet.gender)
                put(ItemsDbSchema.PetTable.Cols.NEUTERED, pet.neutered)
                put(ItemsDbSchema.PetTable.Cols.WEIGHT, pet.weight)
                put(ItemsDbSchema.PetTable.Cols.IMAGE_URI, pet.imageuri)
            }
            db.insert(ItemsDbSchema.PetTable.NAME, null, values)
        }
    }

    fun PetList(): List<Pet> {
        val pets = mutableListOf<Pet>()
        readableDatabase.use{ db->
            db.query(ItemsDbSchema.PetTable.NAME, null, null, null, null, null, null).use{ cursor->
                val wrapper = ItemCursorWrapper(cursor)
                while (wrapper.moveToNext()) {
                    pets.add(wrapper.getPet())
                }
            }
        }
        return pets
    }

    fun deletePetAndAllRelatedData(petId: Int) {
         writableDatabase.use{ db->
             db.beginTransaction()
             try {
                 db.delete("weights", "pet_id = ?", arrayOf(petId.toString()))
                 db.delete("reminders", "pet_id = ?", arrayOf(petId.toString()))
                 db.delete("records", "pet_id = ?", arrayOf(petId.toString()))
                 db.delete("pets", "id = ?", arrayOf(petId.toString()))
                 db.setTransactionSuccessful()
             } finally {
                 db.endTransaction()
             }
         }
    }

    fun insertRecord(record: PetRecord){
        writableDatabase.use{ db->
            val values = ContentValues().apply{
                put("pet_id", record.petId)
                put("title", record.title)
                put("date", record.date)
                put("description", record.description)
                put("image_uri", record.recordImageUri)
            }
            db.insert("records",null,values)
        }
    }

    fun getAllRecords(): List<PetRecord> {
        val records = mutableListOf<PetRecord>()
        readableDatabase.use{ db->
            db.query("records",
            null, null, null, null, null,
            "date DESC").use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val petId = cursor.getInt(cursor.getColumnIndexOrThrow("pet_id"))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                    val imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"))
                    val record = PetRecord(id, petId, title, date, description, imageUri)
                    records.add(record)
                }
            }
        }
        return records
    }

    fun deleteRecord(recordId: Int) {
        writableDatabase.use{db->
            db.delete("records", "id = ?", arrayOf(recordId.toString()))
        }
    }

    fun insertReminder(reminder: PetReminder) {
       writableDatabase.use { db ->
           val values = ContentValues().apply {
               put("pet_id", reminder.petId)
               put("title", reminder.title)
               put("notes", reminder.notes)
               put("date", reminder.date)
           }
           db.insert("reminders", null, values)
       }
    }

    fun getReminders(): List<PetReminder> {
        val reminders = mutableListOf<PetReminder>()
        readableDatabase.use{db->
            db.query("reminders", null, null, null, null, null, "date ASC").use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val petId = cursor.getInt(cursor.getColumnIndexOrThrow("pet_id"))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                    val notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    reminders.add(PetReminder(id, petId, title, notes, date))
                }
            }
        }
        return reminders
    }

    fun deleteReminder(reminderId: Int) {
        writableDatabase.use{ db->
            db.delete("reminders", "id = ?", arrayOf(reminderId.toString()))
        }
    }

    fun insertWeight(weightRecord: PetWeight) {
        writableDatabase.use{ db->
            val values = ContentValues().apply {
                put("pet_id", weightRecord.petId)
                put("weight", weightRecord.weight)
                put("date", weightRecord.date)
            }
            db.insert("weights", null, values)
        }
    }

    fun getWeightsForPet(petId: Int): List<PetWeight> {
        val weights = mutableListOf<PetWeight>()
        readableDatabase.use{ db->
            db.query("weights", null, "pet_id = ?", arrayOf(petId.toString()), null, null, "date DESC").use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val weight = cursor.getString(cursor.getColumnIndexOrThrow("weight"))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    weights.add(PetWeight(id, petId, weight, date))
                }
            }
        }
        return weights
    }

    fun updatePetWeight(petId: Int, newWeight: String) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put("weight", newWeight)
            }
            db.update(ItemsDbSchema.PetTable.NAME, values, "id = ?", arrayOf(petId.toString()))
        }
    }
}