package com.markoid.manager.extensions

import android.view.View
import com.google.android.gms.maps.MapView

fun MapView.hideLocationButton() {
    val locationButton =
        (this.findViewById<View>(Integer.parseInt("1")).parent as View)
            .findViewById<View>(Integer.parseInt("2"))
    locationButton.visibility = View.GONE
}