mvn clean install
adb install -r target/ybo-tv-android-1.3.2-SNAPSHOT.apk
adb shell am start -n fr.ybo.ybotv.android/.activity.LoadingActivity
