set APK=release/app-release.apk
cd /d ../
if exist %APK% (
    adb install -rtd %APK%
) else (
    gradlew installAdeveloperDebug --info --warning-mode all
)
pause

