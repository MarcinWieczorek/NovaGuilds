#!/usr/bin/env bash

echo " ******* NovaGuilds *******"
echo "Downloading changes..."
git pull
echo "Done."
echo ""

echo "What version do you want to build?"
echo "stable"
echo "dev"
git tag -l

echo -n "Answer: "
read answer

if [ ${answer} == "stable" ]; then
    git checkout master
elif [ ${answer} == "dev" ]; then
    git checkout develop
else
    if git checkout "tags/${answer}"; then
        echo "Selected tag: "${answer}
    else
        exit
    fi
fi

mvn clean install

echo " *******"
echo "Desired file can be found in target/ directory"
find target/ -maxdepth 1 -type f -name "NovaGuilds*"
echo " ******* NovaGuilds *******"
