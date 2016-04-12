Requirements:
-> adb

Installation:
-> Connect your phone in USB-Debugging mode.
-> Use the command : adb install app/build/outputs/apk/app-debug.apk
-> The app is now installed on your phone succesfully.

Usage:
-> To toggle wifi, use the following command on your command line:
	adb shell am start -n com.robustest.nizedha/.MainActivity -e wifi true/false

-> To toggle gps(API level < 20), use the following command on your command line:
	adb shell am start -n com.robustest.nizedha/.MainActivity -e gps true/false

-> To mock location, use the following command on your command line:
	adb shell am start -n com.robustest.nizedha/.MainActivity -e location "<lattitude(in degrees/decimals),longitude(in degrees/decimals),accuracy>"

	Note: Make sure "Allow mock locations" is enabled on your device.

-> to read the sms present in the inbox of the phone, use the following command on your command line:
	adb shell am start -n com.robustest.nizedha/.MainActivity -e sms <number of sms to read>

	Note: The sms is stored in data/data/com.robustest.nizedha/files/sms_data.json (in json format) sorted accordigng to their id's.

   to see the data use the following command on your command line:

	adb shell run-as com.robustest.nizedha cat files/sms_data.json

-> to delete all the sms at once:
	
	if android > KitKat, first change the default to Nizedha in settings, then use the following command:
		adb shell am start -n com.robustest.nizedha/.MainActivity -e clean inbox

-> to delete limited number of sms:

	if android > KitKat, first change the default to Nizedha in settings, then use the following command:
		adb shell am start -n com.robustest.nizedha/.MainActivity -e clean <number>
	
-> to clean the entire call log, use the following command:
	adb shell am start -n com.robustest.nizedha/.MainActivity -e clean log