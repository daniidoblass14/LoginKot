package com.example.login

class Respuesta {

    private var id = 0
    private var mensaje: String? = null

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getMensaje(): String? {
        return mensaje
    }

    fun setMensaje(mensaje: String?) {
        this.mensaje = mensaje
    }

}