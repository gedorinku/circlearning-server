#!/bin/sh

echo "deploying..."
cd /home/gedorinku/study-battle-server/StudyBattleServer
./gradlew --stop
git pull
nohup ./gradlew run &
echo "deployed🍣 !"

