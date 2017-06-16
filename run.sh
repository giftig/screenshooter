#!/bin/bash

DIR="$(readlink -f "$(dirname "$0")")"

SCALA_VERSION='2.12'
APP_NAME='screenshooter'
APP_VERSION='0.0.1-SNAPSHOT'

for f in $(find "$DIR/lib_managed" -name '*.jar'); do
  CLASSPATH="$CLASSPATH:$f"
done

CLASSPATH="$CLASSPATH:$DIR/target/scala-$SCALA_VERSION/${APP_NAME}_${SCALA_VERSION}-$APP_VERSION.jar"
export CLASSPATH

echo $CLASSPATH

java com.xantoria.screenshooter.Main
