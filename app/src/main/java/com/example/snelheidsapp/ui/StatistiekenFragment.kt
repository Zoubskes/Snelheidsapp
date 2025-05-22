package com.example.snelheidsapp.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.snelheidsapp.R
import com.example.snelheidsapp.data.MeldingenDatabaseHelper

class StatistiekenFragment : Fragment() {

    private lateinit var dbHelper: MeldingenDatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_statistieken, container, false)
        val containerLayout = view.findViewById<LinearLayout>(R.id.statistieken_container)

        dbHelper = MeldingenDatabaseHelper(requireContext())
        val data = dbHelper.getAantalMeldingenPerGebruiker()

        for ((naam, aantal) in data) {
            val textView = TextView(requireContext()).apply {
                text = "$naam → $aantal overtreding(en)"
                textSize = 18f
                setPadding(0, 16, 0, 16)
            }
            containerLayout.addView(textView)
        }

        return view
    }
}
