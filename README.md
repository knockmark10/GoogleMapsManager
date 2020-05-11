# Welcome to Google Maps Manager

This library is meant to reduce boilerplate in your projects when using Gooogle Maps. Custom extensions and manager are available to handle maps with ease.

# **Setup**
[![](https://jitpack.io/v/knockmark10/GoogleMapsManager.svg)](https://jitpack.io/#knockmark10/GoogleMapsManager)

This library is available _via_ [Jitpack](https://jitpack.io/#knockmark10/GoogleMapsManager)

 1. Add it in your root build.gradle at the end of repositories:

```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

 2. Add the dependency:
```java
dependencies {
    implementation 'com.github.knockmark10:GoogleMapsManager:$version'
}
```

# Classes and methods

## MapsManager

### Constructor

|Constructor  | Definition |  
|--|--|
|Context  |Apps context |  
|GoogleMap |GoogleMap object from google maps SDK. |

### Available methods

Public vailable methods are the following: 

|Name|Parameters|Definition|
|--|--|--|
|getIconFromDrawable|Drawable|Turns drawable into bitmap to use it as icon for MarkerOptions|
|addMarker|String, MarkerOptions|Add a marker to the list, while saving a private list of markers for further queries.|
|getAllMarkers|-|Get all the markers added to the map|
|getMarkerBy|Function|Returns a single marker that matches the condition|
|clearMakers|-|Removes all markers from map, and from internal list.|
|setMapStyle|MapStyles|Setting custom style to map|
|setDaylightStyle|-|Switches style according to daylight|
|setCurrentPosition|LatLng, Float|Sets the camera to desired position with custom zoom|
|centerCamera|List\<Marker>, Float|Centers the camera with a list of marker provided, and desired zoom.|
|centerMarkers|List\<LatLng>|Centers the camera with a list of positions provided.|
|drawCircle|LatLng, Double, Int, Int, Int|Draws a circle in the map with custom properties.|

## Map styles class
The available styles at the moment: 
|Style name|Parameters|Description|
|--|--|--|
|Aubergine|-|Sets aubergine style|
|Dark|-|Sets dark style|
|Night|-|Sets night style|
|Retro|-|Sets retro style|
|Silver|-|Sets silver style|
|Default|-|Default map without any style|
|Custom|Raw resource|Custom style|

# Usage

```kotlin
val manager = MapsManager(context,googleMap)
// Add marker to the map
manager.addMarker("id", markerOptions)
// Set style to map
manager.setMapStyle(Mapstyles.Custom(R.raw.custom_style))
...
//Delete location icon from map
this.map_view.hideLocationButton()
```

