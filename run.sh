#!/bin/bash

java -Dfile.encoding=UTF-8 -cp ./out/production/Interpreteur:./out/production/ServerAS/:./lib/json-20140107.jar:./lib/snakeyaml-1.28.jar:./lib/annotations-19.0.0.jar:./lib/javax.websocket-api-1.1.jar:./lib/dotenv-java-2.2.0.jar server.Server
