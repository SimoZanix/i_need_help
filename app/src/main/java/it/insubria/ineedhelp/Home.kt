package it.insubria.ineedhelp

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Timer

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val elementiSelezionati: MutableList<String> = mutableListOf()
    val elementiEstratti: MutableList<String> = mutableListOf()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latitude: String? = null
    private var longitude: String? = null
    private val uiHandler = Handler(Looper.getMainLooper())
    private var flag = false
    private val forMax = 9
    private var ThreadSMS = Thread{
        while (!Thread.currentThread().isInterrupted && !flag){
            try {
                for (i in 0..forMax){
                    sendSMS()
                    if (i == forMax){
                        uiHandler.post {
                            Toast.makeText(requireContext(), "all messages were sent successfully", Toast.LENGTH_SHORT).show()
                        }
                        flag = true
                    }
                    Thread.sleep(10000)
                }
            }catch (e: InterruptedException){

                Thread.currentThread().interrupt()
            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onPause() {//onPause quando il fragment cambia e non è piu quello della home si attiva questa funzione dove interrompe il thread
        super.onPause()

        ThreadSMS.interrupt()
    }



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(// creazione del adapter per la listview dove verranno messi tutti i contatti presi da un database locale e due bottoni dove manda i messaggi agli utenti selezionati oppure elimina il contatto
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val db = MyDbHelper(requireContext())
        var data = db.getAllContacts()
        val listView = view.findViewById<ListView>(R.id.list_view)
        if (data.size > 0){
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice, data)
            listView.adapter = adapter
        }
        if (data.size == 0){
            data.add("put a new contact in the ADD CONTACT section")
            listView.setChoiceMode(ListView.CHOICE_MODE_NONE)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
            listView.adapter = adapter
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val button = view.findViewById<Button>(R.id.button2) // Sostituisci con l'ID del tuo pulsante
        val btndelatecontact = view.findViewById<Button>(R.id.buttonDelateContact)
        listView.setOnItemClickListener { parent, view, position, id ->
            val posizione = position
            val elemento = listView.getItemAtPosition(position) as String
            //var lista = mutableListOf<String>()
            if (!elementiSelezionati.contains(elemento)) {
                elementiSelezionati.add(elemento)
                    val splitElements = elemento.split(" ")
                    val name = splitElements[0]
                    val phoneNumber = splitElements[1]
                    elementiEstratti.add(phoneNumber)

            }else{
                elementiSelezionati.remove(elemento)
                val splitElements = elemento.split(" ")
                elementiEstratti.remove(splitElements[1])

            }

        }

        btndelatecontact.setOnClickListener(){
            if (elementiEstratti.isEmpty()){
                Toast.makeText(requireContext(), "please select a contact", Toast.LENGTH_SHORT).show()
            }else{
                mostraDialogoConfermaEliminazione()
            }


        }
        button.setOnClickListener(){
                if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    if (elementiEstratti.isEmpty()){
                        uiHandler.post {
                            Toast.makeText(requireContext(), "please select a contact", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        try {
                            mostraDialogoAvvisoInvioMessaggio()
                            ThreadSMS.start()
                        }catch (e: Exception){println("Errore durante l'invio degli SMS: ${e.message}")}

                    }



                }else{
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.SEND_SMS), 100)

                }
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(Manifest.permission.SEND_SMS), 100) }
                return@setOnClickListener
            }
        }




        return view
    }

    private fun mostraDialogoConfermaEliminazione() {//mostra avviso per eliminazione del contatto
        val db = MyDbHelper(requireContext())
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Elimination")
            .setMessage("Are you sure you want to delete the contacts?")
            .setPositiveButton("Sì") { dialogInterface, _ ->
                db.delateContacts(elementiEstratti)
                Toast.makeText(requireContext(), "contacts successfully deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun mostraDialogoAvvisoInvioMessaggio() {//mostra avviso di stare nel fragment home mentre manda i messaggi
        val builder = AlertDialog.Builder(context)
        builder.setMessage("To ensure that messages are sent correctly you must stay in the \"HOME\" section and not change")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }


    private fun sendSMS() {//funzione per mandare i messaggi agli utenti selezionati
        val db = MyDbHelper(requireContext())
        var message = ""
        if (elementiEstratti.isEmpty()){

            uiHandler.post {
                Toast.makeText(requireContext(), "please select a contact", Toast.LENGTH_SHORT).show()
            }
        }else{

        if (db.getMessage() == "I'm in an emergency situation and I need help" || db.getMessage().isEmpty() || db.getMessage() == ""){
            message = "I am in an emergency situation and need help, COORDINATE:" + db.getCoordinate()

            uiHandler.post {
                Toast.makeText(requireContext(), "A default emergency message was sent in the absence of a custom one", Toast.LENGTH_SHORT).show()
            }

        }else{
            message = db.getMessage()
            message += " COORDINATE: " + db.getCoordinate()
        }
            for (number in elementiEstratti){
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(number, null, message, null, null)
                uiHandler.post {
                    Toast.makeText(requireContext(), "message sent", Toast.LENGTH_SHORT).show()
                }
                Thread.sleep(2000)
            }
        }




    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            if(grantResults.size >0 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED){
                sendSMS()
            }else{
                Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }




}






