#! /bin/bash

# Usage
if [ -z "$1" ]; then
	echo usage: $0 projectName
	exit 1
fi

cp -r spar-wings-_ spar-wings-$1
sed -i '' -e "s/_/$1/" spar-wings-$1/..project
mv spar-wings-$1/..project spar-wings-$1/.project
echo "include \"spar-wings-$1\"" >>settings.gradle

echo spar-wings-$1 created
