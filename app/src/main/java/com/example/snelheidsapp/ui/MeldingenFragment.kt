package com.example.snelheidsapp.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.snelheidsapp.R
import com.example.snelheidsapp.model.Melding
import com.example.snelheidsapp.data.MeldingenDatabaseHelper

// Fragment waarin de gebruiker meldingen kan bekijken, aanpassen en verwijderen
class MeldingenFragment : Fragment() {

    private lateinit var helper: MeldingenDatabaseHelper // Database helper
    private lateinit var listView: ListView // Lijst van meldingen
    private lateinit var adapter: ArrayAdapter<String> // Adapter voor de lijstweergave

    private var meldingen = mutableListOf<Melding>() // Lijst van alle meldingen van gebruiker
    private var geselecteerdeMelding: Melding? = null // Huidig geselecteerde melding (voor bewerken/verwijderen)

    // Invoervelden
    private lateinit var straatField: EditText
    private lateinit var huisnummerField: EditText
    private lateinit var snelheidField: EditText
    private lateinit var limietField: EditText

    // Wordt aangeroepen bij het maken van de fragmentview
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_meldingen, container, false)

        // Initialiseer database helper
        helper = MeldingenDatabaseHelper(requireContext())

        // Verbind inputvelden met de bijbehorende views
        straatField = view.findViewById(R.id.input_straat)
        huisnummerField = view.findViewById(R.id.input_huisnummer)
        snelheidField = view.findViewById(R.id.input_speed)
        limietField = view.findViewById(R.id.input_limit)

        // Haal gebruikersnaam uit shared preferences
        val gebruikerNaam = requireContext().getSharedPreferences("snelheidsapp_prefs", 0)
            .getString("logged_in_user", "") ?: ""

        // Haal het ID van de ingelogde gebruiker op uit de database
        val gebruikerId = helper.getReadableDatabase().rawQuery(
            "SELECT id FROM gebruiker WHERE gebruikersnaam = ?",
            arrayOf(gebruikerNaam)
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else -1
        }

        // Initialiseer de lijst en laad meldingen voor de gebruiker
        listView = view.findViewById(R.id.list_meldingen)
        laadMeldingen(gebruikerId)

        // Wanneer een melding wordt aangeklikt, vul de velden in met de gegevens
        listView.setOnItemClickListener { _, _, position, _ ->
            geselecteerdeMelding = meldingen[position]
            snelheidField.setText(geselecteerdeMelding?.snelheid.toString())
            limietField.setText(geselecteerdeMelding?.limiet.toString())
            straatField.setText(geselecteerdeMelding?.straat)
            huisnummerField.setText(geselecteerdeMelding?.huisnummer)
        }

        // Update-knop: sla wijzigingen in de geselecteerde melding op
        view.findViewById<Button>(R.id.btn_update).setOnClickListener {
            geselecteerdeMelding?.let {
                it.snelheid = snelheidField.text.toString().toInt()
                it.limiet = limietField.text.toString().toInt()
                it.straat = straatField.text.toString()
                it.huisnummer = huisnummerField.text.toString()
                helper.updateMelding(it)
                laadMeldingen(gebruikerId)
                leegVelden()
            }
        }

        // Verwijder-knop: verwijder de geselecteerde melding
        view.findViewById<Button>(R.id.btn_verwijder).setOnClickListener {
            geselecteerdeMelding?.let {
                helper.verwijderMelding(it.id)
                laadMeldingen(gebruikerId)
                leegVelden()
            }
        }

        return view
    }

    // Laadt alle meldingen van de gebruiker en toont ze in de ListView
    private fun laadMeldingen(gebruikerId: Int) {
        meldingen = helper.haalMeldingenVoorGebruikerOp(gebruikerId).toMutableList()

        // Maak een overzichtelijke string per melding voor weergave in de lijst
        val lijst = meldingen.map {
            "${it.snelheid}km/h in ${it.limiet}km/h straat ${it.straat} ${it.huisnummer}"
        }

        // Zet de lijst in de adapter
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lijst)
        listView.adapter = adapter
    }

    // Leegt alle invoervelden en deselecteert de huidige melding
    private fun leegVelden() {
        snelheidField.setText("")
        limietField.setText("")
        straatField.setText("")
        huisnummerField.setText("")
        geselecteerdeMelding = null
    }
}
