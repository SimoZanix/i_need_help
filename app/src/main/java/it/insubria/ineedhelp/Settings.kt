package it.insubria.ineedhelp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Settings.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val db = MyDbHelper(requireContext())
        val btn = view.findViewById<Button>(R.id.Sendsms)
        val message = view.findViewById<EditText>(R.id.editTextSOS)
        val textviewcoordinates = view.findViewById<TextView>(R.id.textViewCoordinate2)
        var coordinate = db.getCoordinate()
        textviewcoordinates.setText(coordinate)
        var messaggio = message.text
        btn.setOnClickListener(){
            var messaggio_utente = messaggio.toString()
            if (messaggio_utente.isEmpty() || messaggio_utente.equals("")){
                messaggio_utente = "I'm in an emergency situation and I need help"
            }
            var messaggioEsiste = db.getMessageCount()
            if(messaggioEsiste){
                var aggiornato = db.aggiornaMessaggio("1", messaggio_utente)
                if(aggiornato){
                    Toast.makeText(requireContext(), "message updated successfully", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "message not updated", Toast.LENGTH_SHORT).show()
                }
            }else{
                var inserito = db.insertMessage(messaggio_utente)
                if (inserito){
                    Toast.makeText(requireContext(), "Message added successfully", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
                }
            }


        }

        textviewcoordinates.setOnClickListener(){
            val text: String = textviewcoordinates.getText().toString()
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(intent)
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Settings.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Settings().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}