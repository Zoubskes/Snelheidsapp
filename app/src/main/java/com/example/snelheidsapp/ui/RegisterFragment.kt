package com.example.snelheidsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.snelheidsapp.R
import com.example.snelheidsapp.data.MeldingenDatabaseHelper

class RegisterFragment : Fragment() {

    private lateinit var dbHelper: MeldingenDatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        dbHelper = MeldingenDatabaseHelper(requireContext())

        val gebruikersnaam = view.findViewById<EditText>(R.id.register_gebruikersnaam)
        val email = view.findViewById<EditText>(R.id.register_email)
        val wachtwoord = view.findViewById<EditText>(R.id.register_wachtwoord)
        val registreerBtn = view.findViewById<Button>(R.id.register_btn)

        registreerBtn.setOnClickListener {
            val naam = gebruikersnaam.text.toString()
            val em = email.text.toString()
            val ww = wachtwoord.text.toString()

            if (naam.isBlank() || em.isBlank() || ww.isBlank()) {
                Toast.makeText(context, "Vul alles in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.registreerGebruiker(naam, em, ww)
            if (success) {
                val prefs = requireContext().getSharedPreferences("snelheidsapp_prefs", 0)
                prefs.edit().putString("logged_in_user", naam).apply()

                startActivity(Intent(requireContext(), HomeActivity::class.java))
            } else {
                Toast.makeText(context, "Gebruikersnaam bestaat al", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
