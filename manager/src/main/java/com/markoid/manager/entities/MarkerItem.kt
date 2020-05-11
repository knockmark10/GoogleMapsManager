package com.markoid.manager.entities

import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

data class MarkerItem(
    val id: String,
    val marker: Marker
)