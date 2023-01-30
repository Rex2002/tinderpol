

# Tinderpol
This is the source code for the "TinderPol" project for the second-year CS module Mobile Development at DHBW Mannheim. 
The app is written to run natively on Android, an iOS version is _not_ in development yet.
<br />
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

## Features
- Click the "Start Swiping" Button to start swiping through Interpol Red, Yellow and UN notices in a tinder-like manners
  - Swipe left to report the sighting of a criminal
  - If there are multiple pictures for one notice, tap on the left and right of the screen to view them all
  - Swipe up to view more information about the notice
    - Use the map button at the top to view countries related to the notice (like country of birth, lnationality, etc)
    - Use the star button at the top to add this notice to your personal watchlist
    - Swipe down or tap above the infobox to return to the previous view
  - Swipe down to return to the main screen
- View your starred notices at the "starred notices" box
  - Click on the image to view the notice's image in fullscreen, if there are multiple images for one notice use the corresponding button to see them all
  - Click on the name to view more information about the notice, unstar the notice or call the map view
- Open the settings with the button in the top left to
  - Select which notice-types you would like to see (UN, Yellow, Red)
  - Clear your starred notices list
  - Delete your swipe history
  - Synchronize all local datastorages with the online notice database
- Offline-Functionality: When leaving the swipeActivity (while being connected to the internet) 50 notices ahead, 20 previous notices and all your starred notices will be downloaded, so that you can continue swiping when returning without a network connection.

Due to capacity reasons, TinderPol's backend service is currently hosted on a server that only comes online for a while when it receives a first request. Therefore, remote syncing of notices might take a while, as the app might need to send multiple requests in the background. Please be patient.

# Legal Disclaimer
To avoid any, though unlikely but possible, problems concerning the violation of the Terms of Use (https://www.interpol.int/Who-we-are/Terms-of-use) of the Interpol Notices, please be aware that this project is not intended to be used in a commerical way and was exclusively made for educational purposes.
By using (especially the offline-functionality) you acknowledge that this happens because of your own doing and that the developers can not be held liable for any resulting violation of the Terms of Use of Interpol.
