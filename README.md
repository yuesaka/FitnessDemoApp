# FitnessDemoApp
Some assumptions I made when I was creating this demo app:
* All data is stored locally - there is no server involved. Any persistent data is simply stored
in the database on the device.
* For the sake of simplicity the password is simply stored in plain text, so do not use any of
your actual passwords :)
* UI is only implemented for portrait mode.
* The periodic reminder goes off at the top of every hour, only if there is a user logged in.
* Since it uses the step sensor introduced in 4.4 (API level 19), that is the minimum API level.
That said, since I only own a Nexus 5X with Android 7.1.1 (API level 25), it was mainly tested on
 that.
* The leaderboard can be access through the menu
* For debugging purposes, tapping the step count textbox in the daily stat will bring up a
database viewer activity that will allow you to look at the state of the database.