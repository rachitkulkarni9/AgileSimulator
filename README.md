## How to Build and Run

This project uses gradle as its build tool. Specifically, we use Gradle 8.10.1 and Java SDK 21
Therefore, you can use the `gradlew` and `gradlew.bat` executables to run gradle (depending on your platform).

### Building
To build the project, run the following gradle command in the root directory of the project:

### Windows/Command Prompt/PowerShell:
`.\gradlew.bat build`

### Mac/Linux/Git Bash/WSL:
`./gradlew build`

### Running
To run the project, run the following gradle command in the root directory of the project:  

### Windows/Command Prompt/PowerShell:
`.\gradlew.bat run`

### Mac/Linux/Git Bash/WSL:
`./gradlew run`

### Cleaning
To clean the project (remove all build artifacts), run the following gradle command in the root directory of the project:

### Windows/Command Prompt/PowerShell:
`.\gradlew.bat clean`

### Mac/Linux/Git Bash/WSL:
`./gradlew clean`

### Building the Fat Jar
To build the fat jar, run the following gradle command in the root directory of the project:

### Windows/Command Prompt/PowerShell:
`.\gradlew.bat fatJar`

### Mac/Linux/Git Bash/WSL:
`./gradlew fatJar`

### Running the Fat Jar
To run the fat jar, run the following command in the root directory of the project, or double click the jar file:

`java -jar build/libs/ser515-VARNS-0.1-all.jar`