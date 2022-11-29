# GpsDataGUI
The program uses Sodium, an FRP library, and Java Swing to create a GUI that displays modules of information about provided GPS data. The modules include: a display for each GpsTracker that shows some simple data, a display to show each event at the time it is passed to the GUI, a control panel that can be used to set the latitude and longitude range, a display that shows each event within this range at the time it is passed to the GUI, and a distance field for each GpsTracker that displays the distance travelled within the specific range in the last 5 minutes.

### Compiling and running the program
```shell
# create directory for class binaries in root repository
$ mkdir bin

# compile and execute from src directory
cd src
javac -d ../bin -cp :../lib/*:../bin: myGUI.java
java -cp :../lib/*:../bin: myGUI
```

### Compiling and running the tests
```shell
# compile all tests from src directory (after creating bin directory)
cd src
javac -d ../bin -cp :../lib/*:../bin: *_Test.java

# run specific test
java -cp :../lib/*:../bin: org.junit.runner.JUnitCore "TestName"
```
