# Tinderpol
This is the source code for the "TinderPol" project for the third semester's module Mobile Development. 
The app is written to run natively on Android, an iOS version is _not_ in development yet.
<\n>
TinderPol lets you swipe through Interpol's Wanted Persons Notices just like you swipe through people on any dating app. You can check out the latest mugshot fashions, find out more about each wanted person and if somebody seems familiar, you can even contact Interpol straight from the app.
We still have loads of ideas on how to improve the app (see Issues for some concrete ones) but in the end, we're looking for you to like it. So please feel free to tell us about any feedback or suggestions you might have.

## Get started
To get started, check out this repository in Android Studio and add a Google Maps API-Key (instructions below).
With that set, you should be able to run on any emulator or physical device running Android with API-level 24 or higher.
You will need a stable internet connection on first startup and offline functionality is limited to around 70 pre-loaded notices at a time to keep storage use low.


### Google Maps API-Key:
The App can show a map with the general position of a criminal's birth country. In order to use the app properly, a Google Maps API-Key has to be saved to your local properties.
In order to do this, you open the file local.properties in the package Gradle Scripts. There you add the property
MAPS_API_KEY= _YOUR_API_KEY_

You can get your own API-Key from Google developers Maps API for free.

## Important Disclaimer
For financial reasons, TinderPol's backend service is currently hosted on a server that only comes online for a while when it receives a first request. Therefore, remote syncing of notices might take a while, as the app might need to send multiple requests in the background. Please be patient and feel free to contact us if you'd like to contribute to project funding.
