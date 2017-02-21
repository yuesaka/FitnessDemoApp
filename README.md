# FitnessDemoApp
Some assumptions I made when I was creating this demo app:
* All data is stored locally - there is no server involved. Any persistent data is simply stored
in the database on the device.
* For the sake of simplicity the password is simply stored in plain text, so do not use any of
your actual passwords :)
* UI is only implemented for portrait mode.
* The periodic reminder goes off at the top of every hour, only if there is a user logged in.
* It uses the hardware step sensor introduced in Nexus 5 and Android 4.4, so it assumes that the
device and the OS is made after Nexus 5/Android 4.4
(See https://developer.android.com/about/versions/android-4.4.html#UserInput)
* The leaderboard can be access through the menu
* For debugging purposes, tapping the step count textbox in the daily stat will bring up a
database viewer activity that will allow you to look at the state of the database.