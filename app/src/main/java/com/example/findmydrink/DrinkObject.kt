package com.example.findmydrink

import java.io.Serializable

class DrinkObject (
    var idDrink : String = "",
    var strDrink : String = "",
    var strInstructions : String = "",
    var strDrinkThumb : String = ""
        ) : Serializable {
}