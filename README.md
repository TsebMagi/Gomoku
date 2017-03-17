# Gomoku
CS454 Gomoku Project

This is the Project Repository for the Gomoku project made for CS454 at PSU during the Winter 2017 term.

To set up the project:

1. Clone this repository to Android Studio
1. Follow the directions at https://developer.android.com/studio/run/index.html to build the project and run via USB or build an APK to run on your Android device; alternatively, you can launch an emulator to run the app on your computer.

Note: if you do not generate a signed APK you will need to enable the developer options on your particular android device in order to run unsigned APKs


This project makes use of an existing website and server. If you wish to establish your own website and server for running this application:

1. Replace "75.98.169.85" with the IP address of your server in gomoku.html.
1. Replace "www.noahfreed.com" with the IP address of your server in BoardActivity.java.
1. Copy gomoku.html and shoku.js onto a server into the html folder (where webpages are displayed).
1. Run "forever start shoku.js" on the server.
