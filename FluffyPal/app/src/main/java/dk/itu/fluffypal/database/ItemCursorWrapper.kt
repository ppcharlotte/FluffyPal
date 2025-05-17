package dk.itu.fluffypal.database

import android.database.Cursor
import android.database.CursorWrapper
import dk.itu.fluffypal.Pet

class ItemCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getPet(): Pet {
        val id = getInt(getColumnIndexOrThrow(ItemsDbSchema.PetTable.Cols.ID))
        val category = getString(getColumnIndexOrThrow(ItemsDbSchema.PetTable.Cols.CATEGORY))
        val name = getString(getColumnIndexOrThrow(ItemsDbSchema.PetTable.Cols.NAME))
        val birthday = getString(getColumnIndexOrThrow(ItemsDbSchema.PetTable.Cols.BIRTHDAY))
        val arrival = getString(getColumnIndexOrThrow(ItemsDbSchema.PetTable.Cols.ARRIVAL))
        val gender = getString(getColumnIndexOrThrow(ItemsDbSchema.PetTable.Cols.GENDER))
        val neutered = getString(getColumnIndexOrThrow(ItemsDbSchema.PetTable.Cols.NEUTERED))
        val weight = getString(getColumnIndexOrThrow(ItemsDbSchema.PetTable.Cols.WEIGHT))
        val imageuri = getString(getColumnIndexOrThrow(ItemsDbSchema.PetTable.Cols.IMAGE_URI))

        return Pet(id, category, name, birthday, arrival, gender, neutered, weight, imageuri)
    }
}