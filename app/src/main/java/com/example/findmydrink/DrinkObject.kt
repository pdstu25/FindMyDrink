package com.example.findmydrink

import java.io.Serializable

class DrinkObject (
    var strDrink : String = "",
    var idDrink : String = "",
    var strInstructions : String = "",
    var strDrinkThumb : String = ""
        ) : Serializable {
}