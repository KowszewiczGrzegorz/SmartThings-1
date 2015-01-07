
Prerequisites
Get a free Pushbullet account at www.pushbullet.com
Install Minimalistic Text Widgets9 (free), Pushbullet client3 (free) and Tasker4 ($) on your Android device.
Get your API key and device ID from your Pushbullet account page.
Under Pushbullet Setting > Advanced Setting > select copy notes to clipboard.
SmartThings setup

Create a Pushbullet device type with this code16.
This device type holds your Pushbullet credentials and has a function to send a push to your phone.
Configure your Pushbullet device with API key and device ID (device settings via IDE or mobile app).
Create SmartApp with this code12.
This SmartApp monitors events from the devices of interest or mode changes. When any event occurs, the app will send a push after 1 minute, so events that cancel out each other will be filtered out. For example, if you unlock-open-close-lock door within one minute, the overall state of your home doesn't change and a push will not be send. This is done in order to reduce traffic to your mobile device to reduce battery usage. If nothing is happening, a sanity push will be sent every 15 minutes. So the widget is being updated every 15 minutes or when state of the home changes. On the widget screenshot above I display the time-stamp of the latest push so that I know how current my widget is.
When sending a push, the app collects the states of all devices, current mode and time-stamp. If all devices are active, it will return "all" rather than the entire list. For example, when all presence sensors are "away" the state will have values "present: none, notPresent: all". When one of the sensors returns, the state will have values "present: P1, notPresent: P2".

Install SmartApp selecting Pushbullet device above and the devices you want to monitor.

Tasker setup

Create task "Prase Json" with 2 actions as such:
+ Action > Variables > Variable Split. Name: %CLIP, Splitter "SmartThings Home State:".
+ Action > Script > JavaScriplet. Paste the code below:

var map = "["+ global("CLIP2").substring(1) +"]";
var v;
v = JSON.parse(map)[0];
for (i in v) {setGlobal("ST" + i, v[i]); }

This code creates a global Tasker variable for each key in JSON string that represents the state of your home. The global variable will be "ST"+key for each of these keys: "open"/"closed", "locked"/"unlocked", "present"/"notPresent", "on"/"off", "mode", "updated".

Create task "Minimal Text Refresh"
+ Action > Task > Perform Task > "Parse Json". This is to run the task you created in the previous step.
For each value of the state you are interested in displaying on your widget, + Action > Plugin > Minimalistic Text > Variable name: "variable", Variable content: "%STvariable". For example, if you want to display mode, enter Variable name: "mode", Variable content: "%STmode".

Create a new Tasker profile > event > Plugin > Received a Push. The event is configured as such: If I receive a "note" containing the text "SmartThings Home State" from "myself" then "dismiss" or "delete" the push. Select task "Minimal Text Refresh" that you created in the above step.

Create widget

Add widget to your launcher
Select widget size (let's say 4 x 1)
Pick style etc
Under "Layout", add Locale Variable. Enter variable name. For mode, enter "mode".
Add the variables that you would like to display.
This is it. It's really not complicated and should take about 30 minutes to setup a basic widget.

To summarize, the widget works as follows: When an event in ST occurs, a Pushbullet notification containing JSON string is send to your phone. It's intercepted and dismissed by Tasker, it's content is copied into clipboard. Then it's parsed into Tasker global variables and then passed to Minimalist Text widget to be displayed. This could work for any other widgets that are able to display Tasker variables.
