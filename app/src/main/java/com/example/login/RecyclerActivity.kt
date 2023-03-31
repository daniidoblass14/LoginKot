package com.example.login

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidadvance.topsnackbar.TSnackbar

class RecyclerActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recy_layout)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))
        val listaPersonas: List<MyData> =
            obtenerPersonasDeLaBaseDeDatos() // aquí debes implementar tu propia lógica para obtener los datos de la base de datos
        val adapter = MyAdapter(listaPersonas)
        recyclerView!!.setAdapter(adapter)
        adapter.setOnClick(object : MyAdapter.onItemClick {
            override fun onItemClick(data: MyData?) {
                if (data != null) {
                    val intentEditar = Intent(this@RecyclerActivity, Registrar::class.java)
                    intentEditar.putExtra("nombre", data.nombre)
                    intentEditar.putExtra("fecha", data.fecha)
                    startActivity(intentEditar)
                    finish()
                }
            }
        })
        adapter.setOnLongClick(object : MyAdapter.onItemLongClick {
            override fun onItemLongClick(data: MyData?) {
                val nombre: String = data!!.nombre

                //Abrimos la base de datos, de forma escritura.
                val acdbh = ActivitySQLiteHelper(this@RecyclerActivity, "users", null, 1)
                val db: SQLiteDatabase = acdbh.getWritableDatabase()
                try {
                    db.execSQL("DELETE FROM users WHERE user ='$nombre'")
                    val refresh = Intent(this@RecyclerActivity, RecyclerActivity::class.java)
                    startActivity(refresh)
                } catch (e: Exception) {
                    TSnackbar.make(findViewById(android.R.id.content),"Error al eliminar el usuario",TSnackbar.LENGTH_LONG).show();
                }
                acdbh.close()
                db.close()
                finish()
            }
        })
    }

    private fun obtenerPersonasDeLaBaseDeDatos(): List<MyData> {
        val listaPersonas: MutableList<MyData> = ArrayList<MyData>()
        val acdbh = ActivitySQLiteHelper(this@RecyclerActivity, "users", null, 1)
        val db: SQLiteDatabase = acdbh.getReadableDatabase()
        val cursor = db.rawQuery("SELECT user, date, photo FROM users", null)
        while (cursor.moveToNext()) {
            val user = cursor.getString(0)
            val date = cursor.getString(1)
            val photoBytes = cursor.getBlob(2)
            val photoBitmap = if (photoBytes != null && photoBytes.size > 0) BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size) else null
            val data = MyData(user, date, photoBitmap)
            listaPersonas.add(data)
        }
        // Cerrar el cursor y la base de datos
        cursor.close()
        db.close()
        return listaPersonas
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Advertencia")
        builder.setMessage("¿Desea volver hacia atrás?")
        builder.setPositiveButton(
            "Aceptar"
        ) { dialogInterface, i -> salir() }
        builder.setNegativeButton("Cancelar", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun salir() {
        super.onBackPressed()
    }
}
