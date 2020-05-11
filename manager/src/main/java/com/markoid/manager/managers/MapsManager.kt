package com.markoid.manager.managers

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.markoid.manager.R
import com.markoid.manager.entities.MarkerItem
import com.markoid.manager.enums.MapStyles
import java.util.*

class MapsManager(
    private val mContext: Context,
    private val mGoogleMap: GoogleMap
) {

    private val mMarkersList = mutableListOf<MarkerItem>()

    private val inflater: LayoutInflater
        get() = this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    /**
     * Turns drawable into bitmap to use as a marker
     */
    fun getIconFromDrawable(@DrawableRes drawable: Int): BitmapDescriptor? {
        val icon = getMarkerBitmapFromView(drawable)
        return BitmapDescriptorFactory.fromBitmap(icon)
    }

    /**
     * Adds marker into the map, while saving an internal list for every marker.
     */
    fun addMarker(id: String, markerOptions: MarkerOptions) {
        val marker = this.mGoogleMap.addMarker(markerOptions)
        this.saveMarker(MarkerItem(id, marker))
    }

    /**
     * Get the list of markers added to the map
     */
    fun getAllMarkers(): List<Marker> =
        this.mMarkersList.map { it.marker }

    /**
     * Get a marker with a condition set
     */
    fun getMarkerBy(condition: (item: MarkerItem) -> Boolean): Marker? =
        this.mMarkersList.firstOrNull(condition)?.marker

    /**
     * Clear markers from map
     */
    fun clearMakers() {
        this.mMarkersList.clear()
        this.mGoogleMap.clear()
    }

    /**
     * Choose between Aubergine, Dark, Night,
     * Retro, Silver or default style for
     *           your map
     */
    fun setMapStyle(style: MapStyles) {
        this.mGoogleMap.setMapStyle(style)
    }

    /**
     * Sets the map style according to the daylight
     */
    fun setDaylightStyle() {
        this.mGoogleMap.setDaylightStyle()
    }

    /**
     * Set the camera to desire position with custom zoom
     */
    fun setCurrentPosition(position: LatLng, zoom: Float) {
        this.mGoogleMap.setCurrentPosition(position, zoom)
    }

    /**
     * Centers the camera with a marker list given
     */
    fun centerCamera(markerList: List<Marker>, zoom: Float) {
        this.mGoogleMap.centerCamera(markerList, zoom)
    }

    /**
     * Centers the camera with a positions list given
     */
    fun centerMarkers(positionList: List<LatLng>) {
        this.mGoogleMap.centerMarkers(positionList)
    }

    /**
     *    Draws a circle within the map with the given parameters.
     * You can define the color of the stroke, the color of the circle
     *          itself and the width of the stroke.
     */
    fun drawCircle(
        location: LatLng,
        radius: Double,
        @ColorInt strokeColor: Int = Color.BLACK,
        @ColorInt fillColor: Int = 0x30ff0000,
        strokeWidth: Float = 2f
    ) {
        this.mGoogleMap.drawCircle(location, radius, strokeColor, fillColor, strokeWidth)
    }

    private fun getMarkerBitmapFromView(@DrawableRes resourceId: Int): Bitmap {
        val customMarkerView = this.inflater.inflate(R.layout.custom_marker, null)
        val markerImageView = customMarkerView.findViewById(R.id.custom_marker_icon) as ImageView

        markerImageView.setImageResource(resourceId)
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            0,
            0,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )
        customMarkerView.buildDrawingCache()

        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.measuredWidth, customMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)

        customMarkerView.background?.draw(canvas)

        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    private fun saveMarker(marker: MarkerItem) {
        this.mMarkersList.add(marker)
    }

    private fun GoogleMap.setMapStyle(style: MapStyles) {
        val rawStyle: Int =
            when (style) {
                MapStyles.Aubergine -> R.raw.map_auberine
                MapStyles.Dark -> R.raw.map_dark
                MapStyles.Night -> R.raw.map_night
                MapStyles.Retro -> R.raw.map_retro
                MapStyles.Silver -> R.raw.map_silver
                MapStyles.Default -> R.raw.map_default
                is MapStyles.Custom -> style.rawMap
            }
        try {
            val selectedStyle = MapStyleOptions.loadRawResourceStyle(mContext, rawStyle)
            val success = this.setMapStyle(selectedStyle)
            if (!success) Log.e("Error", "Style parsing failed")
        } catch (e: Resources.NotFoundException) {
            Log.e("Error", "Can't find style")
        }
    }

    private fun GoogleMap.setDaylightStyle() {
        val style: MapStyles =
            when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                in 8..11 -> MapStyles.Default
                in 12..18 -> MapStyles.Retro
                in 19..21 -> MapStyles.Aubergine
                in 22..23 -> MapStyles.Night
                else -> MapStyles.Dark
            }
        this.setMapStyle(style)
    }

    private fun GoogleMap.setCurrentPosition(position: LatLng, zoom: Float) {
        val cameraPosition = CameraPosition.Builder().target(position).zoom(zoom).build()
        this.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun GoogleMap.centerCamera(listMarker: List<Marker>, zoom: Float) {
        var latitude = 0.0
        var longitude = 0.0
        for (marker in listMarker) {
            latitude += marker.position.latitude
            longitude += marker.position.longitude
        }

        if (listMarker.isNotEmpty()) {
            latitude /= listMarker.size
            longitude /= listMarker.size
        }

        val cameraPosition =
            CameraPosition.Builder().target(LatLng(latitude, longitude)).zoom(zoom).build()
        this.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun GoogleMap.centerMarkers(latLngList: List<LatLng>) {
        val latLngBounds = LatLngBounds.Builder()

        latLngList.forEach { latLngBounds.include(it) }

        val bounds = latLngBounds.build()
        val width = mContext.resources.displayMetrics.widthPixels
        val height = mContext.resources.displayMetrics.heightPixels
        val padding = (width * 0.10).toInt()

        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        this.animateCamera(cameraUpdate)
    }

    private fun GoogleMap.drawCircle(
        location: LatLng,
        radius: Double,
        @ColorInt strokeColor: Int = Color.BLACK,
        @ColorInt fillColor: Int = 0x30ff0000,
        strokeWidth: Float = 2f
    ): Circle {
        val circleOptions = CircleOptions().apply {
            center(location)
            radius(radius)
            strokeColor(strokeColor)
            fillColor(fillColor)
            strokeWidth(strokeWidth)
        }

        return this.addCircle(circleOptions)
    }

}

