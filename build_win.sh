#!/bin/bash

if [ -d ./out/production/Interpreteur/ ]
then rm -r -f ./out/production/Interpreteur/
fi
if [ -d ./out/production/ServerAS/ ]
then rm -r -f ./out/production/ServerAS/
fi

javac -encoding UTF-8 -sourcepath ./src/ -d ./out/production/Interpreteur/ ./src/interpreteur/executeur/Executeur.java -cp "./lib/json-20140107.jar;./lib/snakeyaml-1.28.jar;./lib/annotations-19.0.0.jar;./lib/dotenv-java-2.2.0.jar"
javac -encoding UTF-8 -sourcepath ./ServerAS/src/ -d ./out/production/ServerAS/ ./ServerAS/src/server/Server.java -cp "./lib/json-20140107.jar;./lib/snakeyaml-1.28.jar;./lib/annotations-19.0.0.jar;./lib/javax.websocket-api-1.1.jar;./out/production/Interpreteur/;./lib/dotenv-java-2.2.0.jar"
javac -encoding UTF-8 -sourcepath ./ServerAS/src/ -d ./out/production/ServerAS/ ./ServerAS/src/websocketserver/ASWebSocketServer.java -cp "./lib/json-20140107.jar;./lib/snakeyaml-1.28.jar;./lib/annotations-19.0.0.jar;./lib/javax.websocket-api-1.1.jar;./out/production/Interpreteur/;./lib/dotenv-java-2.2.0.jar;./lib/javax.websocket-api-1.1.jar;lib/tyrus-container-grizzly-client-1.15.jar;./lib/tyrus-spi-1.15.jar;./lib/tyrus-core-1.15.jar;./lib/grizzly-framework-2.4.4.jar;./lib/grizzly-http-server-2.4.4.jar;./lib/hamcrest-core-1.3.jar;./lib/tyrus-container-grizzly-server-1.15.jar;./lib/grizzly-http-2.4.4.jar;./lib/tyrus-server-1.15.jar;./lib/tyrus-standalone-client-1.15.jar;./lib/apiguardian-api-1.1.2.jar"
if [ ! -d ./out/production/Interpreteur/interpreteur/regle_et_grammaire ]
then mkdir ./out/production/Interpreteur/interpreteur/regle_et_grammaire
fi
cp ./src/interpreteur/regle_et_grammaire/* ./out/production/Interpreteur/interpreteur/regle_et_grammaire/

if [ ! -d ./out/production/Interpreteur/language/languages ]
then mkdir ./out/production/Interpreteur/language/languages
fi
cp ./src/language/languages/* ./out/production/Interpreteur/language/languages