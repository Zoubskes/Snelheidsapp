package com.example.snelheidsapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.snelheidsapp.R
import com.example.snelheidsapp.model.Melding
import com.example.snelheidsapp.data.MeldingenDatabaseHelper
import kotlin.math.roundToInt

class HomeActivity : AppCompatActivity(), LocationListener {

    private lateinit var snelheidText: TextView
    private lateinit var limietText: TextView
    private lateinit var dbHelper: MeldingenDatabaseHelper
    private var gebruikerId: Int = -1 // wordt geladen uit shared prefs
    private var limiet = 1 // default limiet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialiseer views
        snelheidText = findViewById(R.id.speedText)
        limietText = findViewById(R.id.limietText)
        dbHelper = MeldingenDatabaseHelper(this)

        // Haal ingelogde gebruiker op uit shared preferences
        val prefs = getSharedPreferences("snelheidsapp_prefs", Context.MODE_PRIVATE)
        val gebruikerNaam = prefs.getString("logged_in_user", "") ?: ""
        gebruikerId = dbHelper.getGebruikerId(gebruikerNaam)

        // Vraag GPS-locatie aan
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // Vraag locatie-permissie aan als deze nog niet gegeven is
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        // Start met luisteren naar locatie-updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 5f, this)

        // Fragmentknoppen voor meldingen en statistieken
        findViewById<Button>(R.id.meldingen_button).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, MeldingenFragment())
                .commit()
        }

        findViewById<Button>(R.id.statistieken_button).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, StatistiekenFragment())
                .commit()
        }
    }
    // Bepaalt snelheidslimiet op basis van straatnaam
    private fun getSnelheidslimiet(straat: String): Int {
        val snelwegen = listOf("A2", "A4", "A12", "A20", "A44")
        val nwegen = listOf("N206", "N11", "N44", "N208")

        val straatClean = straat.trim().uppercase()

        return when {
            snelwegen.any { straatClean.contains(it) } -> 100
            nwegen.any { straatClean.contains(it) } -> 80

            straatClean.contains("SCHOOL") ||
                    straatClean.contains("DORPS") ||
                    straatClean.contains("CENTRUM") -> 30

            straatClean.contains("INDUSTRIE") -> 60

            else -> 50
        }
    }

    // Wordt automatisch aangeroepen bij elke locatie-update
    override fun onLocationChanged(location: Location) {
        Log.d("SPEED_DEBUG", "GPS: ${location.speed}")
        val snelheidInKmh = (location.speed * 3.6).roundToInt()
        val straat = location.streetName() ?: "Onbekend"
        val limiet = getSnelheidslimiet(straat)

        // Toon snelheid en limiet op scherm
        snelheidText.text = "$snelheidInKmh KM/H"
        limietText.text = "Limiet: $limiet KM/H"

        // Sla overtreding op in database als snelheid boven limiet is
        if (snelheidInKmh > limiet && gebruikerId != -1) {
            val melding = Melding(
                id = 0,
                gebruikerId = gebruikerId,
                snelheid = snelheidInKmh,
                limiet = limiet,
                straat = straat,
                huisnummer = "-"
            )
            dbHelper.voegMeldingToe(melding)
        }

    }

    // Extensie-functie om straatnaam op te halen via Geocoder
    private fun Location.streetName(): String? {
        val geocoder = Geocoder(this@HomeActivity)
        return try {
            val adres = geocoder.getFromLocation(latitude, longitude, 1)
            adres?.firstOrNull()?.thoroughfare
        } catch (e: Exception) {
            null
        }
    }

    // Resultaat van permissie-aanvraag verwerken
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // do nothing
        } else {
            Toast.makeText(this, "Locatie permissie vereist", Toast.LENGTH_SHORT).show()
        }
    }

    // Zorgt ervoor dat GPS-updates hervat worden bij terugkeren naar scherm
    override fun onResume() {
        super.onResume()
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 5f, this)
        }
    }
}
