package com.example.snelheidsapp.data

import android.provider.BaseColumns

object GebruikerDataContract {

    // Definieert de kolomnamen voor de 'gebruiker'-tabel.
    object Gebruiker : BaseColumns {
        const val TABLE_NAME = "gebruiker"
        const val COLUMN_GEBRUIKERSNAAM = "gebruikersnaam"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_WACHTWOORD = "wachtwoord"
        const val COLUMN_AANMAAKDATUM = "aanmaakdatum"
    }

    // Definieert de kolommen voor de 'melding'-tabel.
    object Melding : BaseColumns {
        const val TABLE_NAME = "melding"
        const val COLUMN_GEBRUIKER_ID = "gebruikerId"
        const val COLUMN_SNELHEID = "snelheid"
        const val COLUMN_LIMIET = "limiet"
        const val COLUMN_STRAAT = "straat"
        const val COLUMN_HUISNUMMER = "huisnummer"
    }

    // Definieert de kolommen voor de 'statistiek'-tabel.
    object Statistiek : BaseColumns {
        const val TABLE_NAME = "statistiek"
        const val COLUMN_GEBRUIKER_ID = "gebruikerId"
        const val COLUMN_AANTAL_OVERTREDINGEN = "aantal"
    }
}
