package com.example.snelheidsapp.data

import android.provider.BaseColumns

object DbStatements {


    object Gebruiker {
        const val CREATE = """
            CREATE TABLE ${GebruikerDataContract.Gebruiker.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${GebruikerDataContract.Gebruiker.COLUMN_GEBRUIKERSNAAM} TEXT UNIQUE,
                ${GebruikerDataContract.Gebruiker.COLUMN_EMAIL} TEXT,
                ${GebruikerDataContract.Gebruiker.COLUMN_WACHTWOORD} TEXT,
                ${GebruikerDataContract.Gebruiker.COLUMN_AANMAAKDATUM} TEXT
            )
        """
        const val DROP = "DROP TABLE IF EXISTS ${GebruikerDataContract.Gebruiker.TABLE_NAME}"
        // SQL-statement om de Gebruiker-tabel te verwijderen als die al bestaat.
    }

    object Melding {
        const val CREATE = """
            CREATE TABLE ${GebruikerDataContract.Melding.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${GebruikerDataContract.Melding.COLUMN_GEBRUIKER_ID} INTEGER,
                ${GebruikerDataContract.Melding.COLUMN_SNELHEID} INTEGER,
                ${GebruikerDataContract.Melding.COLUMN_LIMIET} INTEGER,
                ${GebruikerDataContract.Melding.COLUMN_STRAAT} TEXT,
                ${GebruikerDataContract.Melding.COLUMN_HUISNUMMER} TEXT,
                FOREIGN KEY(${GebruikerDataContract.Melding.COLUMN_GEBRUIKER_ID}) 
                    REFERENCES ${GebruikerDataContract.Gebruiker.TABLE_NAME}(${BaseColumns._ID})
            )
        """
        const val DROP = "DROP TABLE IF EXISTS ${GebruikerDataContract.Melding.TABLE_NAME}"
        // Statement om de Melding-tabel te verwijderen.
    }

    object Statistiek {
        const val CREATE = """
            CREATE TABLE ${GebruikerDataContract.Statistiek.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${GebruikerDataContract.Statistiek.COLUMN_GEBRUIKER_ID} INTEGER,
                ${GebruikerDataContract.Statistiek.COLUMN_AANTAL_OVERTREDINGEN} INTEGER,
                FOREIGN KEY(${GebruikerDataContract.Statistiek.COLUMN_GEBRUIKER_ID}) 
                    REFERENCES ${GebruikerDataContract.Gebruiker.TABLE_NAME}(${BaseColumns._ID})
            )
        """
        const val DROP = "DROP TABLE IF EXISTS ${GebruikerDataContract.Statistiek.TABLE_NAME}"
        // Statement om de Statistiek-tabel te verwijderen.
    }
}
