@echo off
if exist class\webserver rmdir /S /Q class\webserver
javac -sourcepath .\src\main\java\ -classpath .\class\ -d .\class\ .\src\main\java\webserver\Program.java
java -cp .\class\ webserver.Program "webroot"