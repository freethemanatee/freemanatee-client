#!/usr/bin/env bash

PROJECT_DIR="$(pwd)"

# we change directories here instead of calling rm -f <path> directly,
# because i run bash on windows to use this right now and some wildcards operations are broken.
# also im too lazy to fix my setup and probably some other people are too. :seenoevil:
cd "$APPDATA"/.minecraft/mods/1.12.2 || exit
rm -f KAMI-*-release.jar
rm -f hephaestus-*.jar

cd "$PROJECT_DIR"/build/libs/ || exit
cp hephaestus-*-release.jar "$APPDATA"/.minecraft/mods/1.12.2/
