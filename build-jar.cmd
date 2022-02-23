@echo off
javac -sourcepath .\src\main\java\ -classpath .\class\ -d .\class\ .\src\main\java\webserver\Program.java
jar -cfe ServerProgram.jar webserver.Program -C .\class\ webserver