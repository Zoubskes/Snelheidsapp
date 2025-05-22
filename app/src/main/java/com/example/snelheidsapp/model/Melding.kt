package com.example.snelheidsapp.model

data class Melding(
    var id: Int = 0,
    var gebruikerId: Int,
    var snelheid: Int,
    var limiet: Int,
    var straat: String,
    var huisnummer: String
){

}
