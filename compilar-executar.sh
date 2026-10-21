#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes
javac --release 17 -encoding UTF-8 -d build/classes -sourcepath src/main/java src/main/java/fitlife/Main.java
jar -J-Djava.io.tmpdir=build --create --file build/fitlife.jar --main-class fitlife.Main -C build/classes .
java -Dfile.encoding=UTF-8 -jar build/fitlife.jar
