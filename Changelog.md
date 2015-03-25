# Changelog #

## 0.7.1 ##
  * fix: notification system problems on ICS
  * fix: fetching moon image (for now default moon image is used for ALL moons)
  * fix: moon activity showing in two rows
  * fix: fetching unread messages on overview
  * fix: not able to send espionage or launch missales if number was ending with 0
  * tweak: planet list tweak (moon is shown in same row as planet -  clicking on moon image will switch to moon)
  * new: removed Resource settings from context menu as it is available from scroll menu
  * new: changed login layout so it could fit on smaller resolution

## 0.7.0 ##
  * Some fixes to support server version 4.0.3

## 0.6.7 ##
  * Some fixes so application can work with server version 3.1.0
  * Removed Attack action from galaxy view menu if player are the owner of selected planet
  * Galaxy view ally info
  * Added or improved some indicators, like: loading indicator in galaxy view, planet activity indicator, moon activity indicator
  * Mark debris in green if there are equals or more then configured recyclers needed (default set to 2)
  * Added Honorable target mark in galaxy view
  * Some minor fixes and improvements

## 0.6.6 ##
  * Fixed login errors for server version 2.3.0

## 0.6.5 ##
  * update to server version 2.2.5
  * Disabled market-only features
  * Removed resource settings from context menu
  * Added resource settings to tab menu
  * Added abandon planet function
  * Further improved loading and crash reduction

## 0.6.4 ##
  * Added Spanish Translation
  * update to server version 2.1.4
  * improved handling of server updates

## 0.6.3 ##
  * Fixed crash on single planet accounts
  * Fixed changes for 2.1.3 server update
  * Added better errorhandling for further server updates
  * Added Polish Translation

## 0.6.2 ##
  * Added missing planet icons
  * Added folders to message box (**Will show up for everyone but only work if you have commander**)
  * Added list of Buddies to select from when sending a new message
  * Fixed icon for small transporter

## 0.6.1 ##
  * Added French translation
  * Fixed Fleet Movement not showing hostile fleets
  * Fixed wrong shipcount on "New Mission"

## 0.6 ##
  * Added sound alert to notification
  * Added unread messages to notification
  * Changed layout of notification
  * Added Menu (Attack, Transport) to galaxy view
  * Added alliance view (currently only showing ally-webpage)
  * Added function to show a combat report
  * Added setting to select if message should be show as html or text
  * Added function to change to color of the tabs in the settings
  * Added color to message subject in message list
  * Fixed crash on Overview
  * Fixed moon not showing in title when selected
  * Fixed overlapping on bottom bar
  * Fixed some minor bugs
  * Updated countries

## 0.5 ##
  * Added new feature to send fleet (Menu -> Fleet -> New Mission)
  * Added clickable links in messages (Planet coordinates, attack link) ([Issue 28](https://code.google.com/p/ogameandroid/issues/detail?id=28))
  * Added planet index in galaxy view ([Issue 27](https://code.google.com/p/ogameandroid/issues/detail?id=27))
  * Added possibility to rename planet (longpress on planet icon)
  * Fixed message when trying to build shield dome more than once ([Issue 35](https://code.google.com/p/ogameandroid/issues/detail?id=35))
  * Fixed error preventing user from add defense or ship to cue more than once ([Issue 26](https://code.google.com/p/ogameandroid/issues/detail?id=26))

## 0.4.5 ##
  * Added Galaxy View (see [Screenshots](Screenshots.md))
  * Fixed crash when selecting last planet while having at least one moon ([Issue 24](https://code.google.com/p/ogameandroid/issues/detail?id=24))

## 0.4.4 ##
  * Added planet and moon images to local storage
  * Added needed build time
  * Added cancel function for buildings and research
  * Added list of requirements for disabled objects
  * Added buildable in X time display
  * Reworked current resources update to calculate instead of request
  * Fixed missing linefeed in fleetinfo

## 0.4.3 ##
  * Added delete all messages button
  * Added dutch translation

## 0.4.2 ##
  * New icon (if you got a better one, feel free to tell me)
  * Reworked notification system
  * Change color of title text
  * Hiding resource info if value is 0
  * Added energy information where useful
  * Improved message view
  * Enabled message delete button (other buttons coming soon)
  * disabled "new mission" button on fleetview (had no function anyway)

## 0.4.1 ##
  * Fixed not displaying field count on overview tab
  * Improved message list (see [here](http://image.cp-g.de/pics/4d20d1708cebf.png))

## 0.4.0 ##
  * Added moons to the planet list ([Issue 11](https://code.google.com/p/ogameandroid/issues/detail?id=11))
  * Added icons an values for moon buildings
  * Changed User Agent to default agent for Android 2.2.1

## 0.3.2 ##
  * fixed countdown stacking
  * added (readonly) message system

## 0.3.1 ##
  * Completed translation for English and German
  * Added function to reload the universe-list
  * Fixed crash when loading universe-list without network connection ([Issue 10](https://code.google.com/p/ogameandroid/issues/detail?id=10))
  * Fixed freeze/crash with wrong login
  * Added PayPal-Donate Button

## 0.3 ##
  * Public beta release