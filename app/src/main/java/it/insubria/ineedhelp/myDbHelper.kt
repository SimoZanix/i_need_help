package it.insubria.ineedhelp
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

var DATABASENAME = "DB_SOS_MESSAGE"
var DBVERSION = 3
var TABLE_NAME_CONTACTS = "contatti"
var TABLE_NAME_MESSAGE = "message"
var TABLE_NAME_COORDINATE = "coordinate"
var COL_ID = "id"
var COL_NAME = "nome"
var COL_NUMBER = "numero_cellulare"
var COL_MESSAGGIO = "messaggio"
var COL_LONGITUDE = "longitude"
var COL_LATITUDE = "latitude"
class MyDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASENAME, null, DBVERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CONTACTS + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT," +
                COL_NUMBER + " TEXT" +
                ")")

        db?.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MESSAGE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_MESSAGGIO + " VARCHAR(128)" +
                ")")

        db?.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_COORDINATE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_LONGITUDE + " VARCHAR(128)," +
                COL_LATITUDE + " VARCHAR(128)" +
                ")")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONTACTS)
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MESSAGE)
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COORDINATE)
        onCreate(db)
    }

    fun insertContact(nome: String, numeroCellulare: String): Boolean{// inserisce un contatto nella tabella dei contatti
        var db = this.writableDatabase
        var values = ContentValues()
        values.put("nome", nome)
        values.put("numero_cellulare", numeroCellulare)
        var result = db.insert(TABLE_NAME_CONTACTS, null, values)
        var warning = true
        if(result < 0){
            warning = false
        }else{

        }
        db.close()
        return warning
    }

    fun insertMessage(message: String): Boolean{// inserisce il messaggio SOS nella tabella del messaggio
        var db = this.writableDatabase
        var values = ContentValues()
        values.put(COL_MESSAGGIO, message)
        var result = db.insert(TABLE_NAME_MESSAGE, null, values)
        var warning = true
        if(result < 0){
            warning = false
        }else{

        }
        db.close()
        return warning
    }

    fun insertCoordinate(latitude: String, longitude: String): Boolean{// inserisce la latitudine e la longitudine nella tabella delle coordinate
        var db = this.writableDatabase
        var values = ContentValues()
        values.put(COL_LATITUDE, latitude)
        values.put(COL_LONGITUDE, longitude)
        var result = db.insert(TABLE_NAME_COORDINATE, null, values)
        var warning = true
        if(result < 0){
            warning = false
        }else{

        }
        db.close()
        return warning
    }

    fun deleteAllContacts() {// elimina tutti i contatti nella tabella contatti
        val db = writableDatabase
        db.delete(TABLE_NAME_CONTACTS, null, null)
        db.close()
    }

    fun delateContacts(contactsToDelete: MutableList<String>){// elimina un numero di contatti dalla tabella dei contatti
        val db = writableDatabase
        for(number_contact in contactsToDelete){
            db.delete(TABLE_NAME_CONTACTS, "$COL_NUMBER=?", arrayOf("$number_contact"))
        }

        db.close()

    }

    fun aggiornaCoordinata(id: String, latitude: String, longitude: String): Boolean{// restituisce true se la coordinata è stata aggiornata, false altrimenti
        val db = writableDatabase
        var warning : Boolean
        val values = ContentValues()
        values.put("latitude", latitude)
        values.put("longitude", longitude)
        val selection = "id = ?"
        val selectionArgs = arrayOf(id)
        val numRowsUpdated = db.update(TABLE_NAME_COORDINATE, values, selection, selectionArgs)
        if(numRowsUpdated > 0){
            warning = true
        }else{
            warning = false
        }
        db.close()
        return warning
    }

    fun getCoordinatesCount(): Boolean{// restituisce true se cè la coordinata nella tabella, false altrimenti
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_NAME_COORDINATE;"
        var warning: Boolean
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0)

            if (count > 0) {
                warning = true
                // Almeno una riga è presente
            } else {
                warning = false
                // La tabella è vuota
            }
        } else {
            warning = false
            // Errore durante l'esecuzione della query
        }

        cursor.close()
        db.close()
        return warning
    }

    fun getAllContacts(): MutableList<String> {// restituisce tutti i contatti nella tabella dei contatti
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_CONTACTS;"
        val cursor = db.rawQuery(query, null)

        val contatti = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val Nome = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
            val NumeroCellulare = cursor.getString(cursor.getColumnIndexOrThrow(COL_NUMBER))

            val contatto = "$Nome $NumeroCellulare"

            contatti.add(contatto)
        }

        cursor.close()
        db.close()
        return contatti
    }

    fun getMessageCount(): Boolean {// restituisce true se cè il messaggio nella tabella, false altrimenti
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_NAME_MESSAGE;"
        var warning: Boolean
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0)

            if (count > 0) {
                warning = true
                // Almeno una riga è presente
            } else {
                warning = false
                // La tabella è vuota
            }
        } else {
            warning = false
            // Errore durante l'esecuzione della query
        }

        cursor.close()
        db.close()
        return warning
    }

    fun aggiornaMessaggio(id: String, messaggio: String): Boolean{// aggiorna nella tabella del messaggio il nuovo messaggio SOS
        val db = writableDatabase
        var warning : Boolean
        val values = ContentValues()
        values.put("messaggio", messaggio)
        val selection = "id = ?"
        val selectionArgs = arrayOf(id)
        val numRowsUpdated = db.update(TABLE_NAME_MESSAGE, values, selection, selectionArgs)
        if(numRowsUpdated > 0){
            warning = true
        }else{
            warning = false
        }
        db.close()
        return warning
    }

    fun getMessage(): String {// restituisce il messaggio SOS
        val db = readableDatabase
        var Messaggio = ""
        val query = "SELECT $COL_MESSAGGIO FROM $TABLE_NAME_MESSAGE;"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()){
            Messaggio = cursor.getString(cursor.getColumnIndexOrThrow(COL_MESSAGGIO))
        }
        cursor.close()
        db.close()
        return Messaggio
    }

    fun getCoordinate(): String {// restituisce le coordinate
        val db = readableDatabase
        var coordinate = ""
        var lattitudine = ""
        var longitudine = ""
        val query = "SELECT $COL_LATITUDE, $COL_LONGITUDE FROM $TABLE_NAME_COORDINATE;"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()){
            lattitudine = cursor.getString(cursor.getColumnIndexOrThrow(COL_LATITUDE))
            longitudine = cursor.getString(cursor.getColumnIndexOrThrow(COL_LONGITUDE))
        }
        coordinate = "$lattitudine $longitudine"
        cursor.close()
        db.close()
        return coordinate
    }

    fun findContact(numero: String): Boolean { // restituisce true se il contatto esiste altrimenti false
        val db = readableDatabase
        var numero_cellulare = numero
        val query = "SELECT * FROM $TABLE_NAME_CONTACTS WHERE numero_cellulare = ?;"
        val cursor = db.rawQuery(query, arrayOf(numero_cellulare))
        val contattoTrovato = cursor.moveToFirst()
        db.close()
        cursor.close()
        return contattoTrovato
    }
}