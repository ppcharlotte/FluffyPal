package dk.itu.fluffypal.database

object ItemsDbSchema {
    object PetTable {
        const val NAME = "pets"

        object Cols {
            const val ID = "id"
            const val CATEGORY = "category"
            const val NAME = "name"
            const val BIRTHDAY = "birthday"
            const val ARRIVAL = "arrival"
            const val GENDER = "gender"
            const val NEUTERED = "neutered"
            const val WEIGHT = "weight"
            const val IMAGE_URI = "image_uri"
        }
    }
}