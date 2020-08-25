./gradlew fatjar
nohup java -Xmx256M -jar build/libs/PimpMyTerm.jar ./aCore.xml > /tmp/pimpMyTerm.log 2>&1 &
sleep 2
tail -100f /tmp/pimpMyTerm.log
