#!/usr/bin/env bash

BASEDIR=$(dirname "$0")
JARNAME="play-store-1-00-01.jar"

echo "[PlayStore][GenerateJar] Script base directory: $BASEDIR"

pushd "$BASEDIR"

echo "[PlayStore][GenerateJar] Pwd: $PWD"
echo "[PlayStore][GenerateJar] Delete: $PWD/app/build/libs/"
rm -r ${BASEDIR}/app/build/libs/

echo "[PlayStore][GenerateJar] Build the jar at: $PWD/app/build/libs/$JARNAME"
./gradlew app:jar

if [ "$#" == "1" ]; then
    echo "[PlayStore][GenerateJar] Copy $PWD/app/build/libs/$JARNAME to $1"
    cp ${PWD}/app/build/libs/${JARNAME} $1
fi

popd