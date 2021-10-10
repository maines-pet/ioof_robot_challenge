# IOOF Robot Challenge

## How to run

### Through IDE
`git clone https://github.com/maines-pet/ioof_robot_challenge.git`

Import to your favourite IDE

Run the `main` method in `main.kt` in your IDE

### Through Maven
If Maven is installed, once you clone the code to your local environment, run 
`mvn clean package` to produce executable jar

Then, run this command in console 

`java -jar target/mainModule-1.0-SNAPSHOT-jar-with-dependencies.jar`

You can also specify an optional argument for the input file by

`java -jar target/mainModule-1.0-SNAPSHOT-jar-with-dependencies.jar ./inputCommands.txt`