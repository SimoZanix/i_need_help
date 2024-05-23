package it.insubria.ineedhelp
//I Need Your Help
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.insubria.ineedhelp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latitude: String? = null
    private var longitude: String? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationUpdate() //aggiornamento coordinate continuo
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->{
                    replaceFragment(Home())
                    true
                }
                R.id.Add_Contact ->{
                    replaceFragment(Add_contact())
                    true
                }
                R.id.Settings ->{
                    replaceFragment(Settings())
                    true
                }
                else ->{
                    true
                }

            }

        }

    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

    private fun getLocationUpdate(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
        val db = MyDbHelper(this)
        val locationRequest = LocationRequest.create()
        locationRequest.setInterval(5000)
        locationRequest.setFastestInterval(3000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // Recupera l'ultima posizione dall'oggetto LocationResult
                val location = locationResult.lastLocation

                // Puoi accedere alle coordinate della posizione come prima:
                val latitude = location?.latitude.toString()
                val longitude = location?.longitude.toString()
                var coordinataEsiste = db.getCoordinatesCount()
                // Inserisci la tua logica per gestire la posizione aggiornata
                // (ad esempio, aggiornare il database o mostrare la posizione su una mappa)
                if(coordinataEsiste){
                    var aggiornato = db.aggiornaCoordinata("1", latitude.toString(), longitude.toString())
                    if(aggiornato){
                        //Toast.makeText(this@MainActivity, "coordinate aggiornate: $latitude $longitude", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@MainActivity, "ERRORE coordinate non aggiornate", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    var inserito = db.insertCoordinate(latitude.toString(), longitude.toString())
                    if(inserito){

                    }else{
                        Toast.makeText(this@MainActivity, "ERRORE coordinate non inserite", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }







}