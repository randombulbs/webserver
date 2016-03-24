# WebServer

A simple extensible web server.
Use sbt run to start the server.
The server currently onlysupport an http 200 OK response for a correct http request with keep-alive option.

curl -XPOST --header "Connection: keep-alive" http://localhost:8080/

curl -XPOST --header "Connection: close" http://localhost:8080/
