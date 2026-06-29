package com.openclassroom.eventorias.core.domain.model

import androidx.annotation.StringRes
import com.openclassroom.eventorias.R

enum class EventCategory (@StringRes val labelResId:Int){
    MUSIC(R.string.category_music),
    ART(R.string.category_art),
    TECH(R.string.category_tech),
    FOOD_DRINK(R.string.category_food_drink),
    LITERATURE(R.string.category_literature),
    CINEMA(R.string.category_cinema),
    CHARITY(R.string.category_charity),
    SPORTS(R.string.category_sports),
    BUSINESS(R.string.category_business),
    LIFESTYLE(R.string.category_lifestyle),
    DIVERSE(R.string.category_diverse)
}