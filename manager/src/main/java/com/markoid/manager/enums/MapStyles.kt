package com.markoid.manager.enums

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

sealed class MapStyles {
    object Aubergine : MapStyles()
    object Dark : MapStyles()
    object Night : MapStyles()
    object Retro : MapStyles()
    object Silver : MapStyles()
    object Default : MapStyles()
    class Custom(@RawRes val rawMap: Int) : MapStyles()
}