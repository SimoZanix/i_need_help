package it.insubria.ineedhelp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Add_contact.newInstance] factory method to
 * create an instance of this fragment.
 */
class Add_contact : Fragment() {
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

    private fun mostraDialogoConfermaEliminazione() {//messaggio di conferma per quando si vuole eliminare tutti i contatti
        val db = MyDbHelper(requireContext())
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Elimination")
            .setMessage("Are you sure you want to delete all contacts?")
            .setPositiveButton("Yes") { dialogInterface, _ ->
                db.deleteAllContacts()
                Toast.makeText(requireContext(), "contacts successfully deleted", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(//aggiunta di contatti con dovuti controlli nel inserimento del nome e numero di cellulare, i contatti aggiunti vanno a finire in un database locale
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_contact, container, false)
        val db = MyDbHelper(requireContext())
        val btn = view.findViewById<Button>(R.id.add_contact_btn)
        val Delatebtn = view.findViewById<Button>(R.id.buttonDelate)
        val name = view.findViewById<EditText>(R.id.editTextName)
        var nome = name.text
        val number = view.findViewById<EditText>(R.id.editTextPhone)
        var num = number.text
        btn.setOnClickListener(){
            var nome_utente = nome.toString()
            var num_cell_utente = num.toString()
            if (db.findContact(num_cell_utente)){
                Toast.makeText(requireContext(), "this contact already exists", Toast.LENGTH_SHORT).show()
            }else{
                if (num_cell_utente.length != 13 || nome_utente.equals("")){
                    Toast.makeText(requireContext(), "error adding contact name or mobile number, try again", Toast.LENGTH_SHORT).show()
                }else{
                    var inserito = db.insertContact(nome_utente, num_cell_utente)
                    if (inserito){
                        Toast.makeText(requireContext(), "added successfully", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
                    }

                }
            }




        }

        Delatebtn.setOnClickListener(){
            mostraDialogoConfermaEliminazione()
        }



        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Add_contact.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Add_contact().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}


