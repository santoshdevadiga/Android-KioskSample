# KioskSample
For building applications for corporate-owned/single-use devices/Kiosk Application. For E.g Point of sale, Advertisement 

There is three way to implement Kiosk 
1) Using background Service 
2) Making Application as  Home Launcher and Disabling all keys and backpress 
3) Task lock and Device Management API

Task Lock and Device Management API
This codebase uses Task Lock and Device Management API to achive Kiosk/Single-use devices application.

For running the sample code, follow below steps :

  -Download this sample code and open in Android Studio
  
  -Connect device and run project
  
  -Open Command line terminal and run Android Debug Bridge Command(ADB) as given below: 
  
    adb shell dpm set-device-owner com.santoshdevadiga.kiosksample/.DeviceAdminReceiver.

    Note: Command line should be open where adb.exe file is present. It will be present inside Android SDK (PATH: Sdk\platform-tools folder).

  -Click on "Enable Kiosk Mode" to set app in kiosk mode.

Minimum Configuration require:
  -Android Studio version 1.0+
  -Test Device with Android 6.0+

