# chatapp
chat application where group users can send messages

## Installation
``` cd chatapp
mvn compile
```

## Server
The server receives all messages and direct it to it's destination(s)
### Run the server
```
cd chatapp
mvn exec:java -Dexec.mainClass="com.network.chatapp.ServerGUI"
```

## Client
The Client sends messages to other clients. The server directs these messages to destinations.
### Run the client
```
cd chatapp
mvn exec:java -Dexec.mainClass="com.network.chatapp.ClientGUI"
```

## Features

