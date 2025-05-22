package com.example.snelheidsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.snelheidsapp.R
import com.example.snelheidsapp.data.MeldingenDatabaseHelper

class LoginFragment : Fragment() {

    private lateinit var dbHelper: MeldingenDatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Initialiseer database helper
        dbHelper = MeldingenDatabaseHelper(requireContext())

        // Verwijzingen naar de invoervelden en de login-knop
        val gebruikersnaam = view.findViewById<EditText>(R.id.login_gebruikersnaam)
        val wachtwoord = view.findViewById<EditText>(R.id.login_wachtwoord)
        val loginBtn = view.findViewById<Button>(R.id.login_btn)

        // Wat gebeurt er als er op de login-knop wordt geklikt
        loginBtn.setOnClickListener {
            val naam = gebruikersnaam.text.toString()
            val ww = wachtwoord.text.toString()

            // Check of beide velden zijn ingevuld
            if (naam.isBlank() || ww.isBlank()) {
                Toast.makeText(context, "Vul alles in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Controleer of login klopt in de database
            val success = dbHelper.loginGelukt(naam, ww)
            if (success) {
                val prefs = requireContext().getSharedPreferences("snelheidsapp_prefs", 0)
                prefs.edit().putString("logged_in_user", naam).apply()

                // Ga naar de HomeActivity (hoofdpagina)
                startActivity(Intent(requireContext(), HomeActivity::class.java))
            } else {
                // Geef foutmelding als login mislukt
                Toast.makeText(context, "Ongeldige login", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
