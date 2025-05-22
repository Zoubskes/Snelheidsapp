package com.example.snelheidsapp.data

import android.content.*
import android.database.Cursor
import android.database.sqlite.*
import android.util.Log
import com.example.snelheidsapp.model.Melding

// Constantes voor database naam en versie
private const val DATABASE_NAME = "snelheidsapp.db"
private const val DATABASE_VERSION = 3 // Bij verhogen wordt de database opnieuw opgebouwd

// SQLiteOpenHelper subclass voor het beheren van databasecreatie en -upgrades
class MeldingenDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DB_DEBUG", "onCreate: gebruiker + melding tabellen aangemaakt")

        // SQL: Maak de gebruiker-tabel aan
        val createGebruiker = """
            CREATE TABLE gebruiker (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                gebruikersnaam TEXT UNIQUE,
                email TEXT,
                wachtwoord TEXT
            )
        """
        db.execSQL(createGebruiker)

        // SQL: Maak de melding-tabel aan met een foreign key naar gebruiker
        val createMelding = """
            CREATE TABLE melding (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                gebruikerId INTEGER,
                snelheid INTEGER,
                limiet INTEGER,
                straat TEXT,
                huisnummer TEXT,
                FOREIGN KEY(gebruikerId) REFERENCES gebruiker(id)
            )
        """
        db.execSQL(createMelding)
    }

    // Wordt uitgevoerd bij een database upgrade (versie wijzigt)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS melding")
        db.execSQL("DROP TABLE IF EXISTS gebruiker")
        onCreate(db)
    }

    // === Gebruiker ===

    // Registreert een nieuwe gebruiker in de database
    fun registreerGebruiker(naam: String, email: String, wachtwoord: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("gebruikersnaam", naam)
            put("email", email)
            put("wachtwoord", wachtwoord)
        }

        // Probeer te inserten, faalt bij dubbele gebruikersnaam
        return try {
            db.insertOrThrow("gebruiker", null, values) != -1L
        } catch (e: Exception) {
            false
        }
    }

    // Controleert of gebruikersnaam + wachtwoord klopt
    fun loginGelukt(naam: String, wachtwoord: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM gebruiker WHERE gebruikersnaam = ? AND wachtwoord = ?",
            arrayOf(naam, wachtwoord)
        )
        val success = cursor.count > 0
        cursor.close()
        return success
    }

    // Haalt het ID op van een gebruiker op basis van gebruikersnaam
    fun getGebruikerId(gebruikersnaam: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM gebruiker WHERE gebruikersnaam = ?",
            arrayOf(gebruikersnaam)
        )
        val id = if (cursor.moveToFirst()) cursor.getInt(0) else -1
        cursor.close()
        return id
    }

    // === Melding ===

    // Voegt een nieuwe melding toe aan de database
    fun voegMeldingToe(melding: Melding) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("gebruikerId", melding.gebruikerId)
            put("snelheid", melding.snelheid)
            put("limiet", melding.limiet)
            put("straat", melding.straat)
            put("huisnummer", melding.huisnummer)
        }
        db.insert("melding", null, values)
    }

    // Haalt alle meldingen op van een specifieke gebruiker
    fun haalMeldingenVoorGebruikerOp(gebruikerId: Int): List<Melding> {
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM melding WHERE gebruikerId = ?",
            arrayOf(gebruikerId.toString())
        )

        // Bouw een lijst van Melding-objecten op uit de resultaten
        val lijst = mutableListOf<Melding>()
        while (cursor.moveToNext()) {
            lijst.add(
                Melding(
                    id = cursor.getInt(0),
                    gebruikerId = cursor.getInt(1),
                    snelheid = cursor.getInt(2),
                    limiet = cursor.getInt(3),
                    straat = cursor.getString(4),
                    huisnummer = cursor.getString(5)
                )
            )
        }
        cursor.close()
        return lijst
    }

    // Verwijdert een melding op basis van ID
    fun verwijderMelding(id: Int) {
        writableDatabase.delete("melding", "id = ?", arrayOf(id.toString()))
    }

    // Update een bestaande melding in de database
    fun updateMelding(melding: Melding) {
        val values = ContentValues().apply {
            put("gebruikerId", melding.gebruikerId)
            put("snelheid", melding.snelheid)
            put("limiet", melding.limiet)
            put("straat", melding.straat)
            put("huisnummer", melding.huisnummer)
        }
        writableDatabase.update("melding", values, "id = ?", arrayOf(melding.id.toString()))
    }

    // Geeft het aantal meldingen per gebruiker terug in een Map
    fun getAantalMeldingenPerGebruiker(): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        val db = readableDatabase

        // SQL-query die gebruikersnamen koppelt aan aantal meldingen
        val query = """
    SELECT g.gebruikersnaam, COUNT(m.id) AS aantal
    FROM gebruiker g
    LEFT JOIN melding m ON g.id = m.gebruikerId
    GROUP BY g.gebruikersnaam
""".trimIndent()

        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val naam = cursor.getString(0)
            val aantal = cursor.getInt(1)
            result[naam] = aantal
        }
        cursor.close()

        return result
    }
}
