import robot.TableTop
import java.io.File

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Program only accepts 1 argument.")
        return
    }

    //Input file mode
    if (args.size == 1) {
        val inputFile = File(args[0])
        if (!inputFile.exists()) {
            println("File doesn't exist. Please try again.")
            return
        } else {
            val tableTop = TableTop()

            inputFile.useLines {
                it.toList().forEach {
                    line -> tableTop.processCommand(line.uppercase())
                }
            }
            return
        }
    }


    //Console Mode
    val tableTop = TableTop()

    val commands = """List of Commands
        |1. PLACE x,y,f        where x and y are integers referring to the x and y position
        |                            f is the direction which the robot is facing (NORTH,EAST,SOUTH,WEST)
        |2. MOVE               moves the robot to the direction it's facing
        |3. LEFT               rotates the robot to the left
        |4. RIGHT              rotates the robot to the right
        |5. ROBOT x            activates the robot with the id = x
        |6. REPORT             prints all the robot position and status as well as the total count of robots
        |7. HELP               prints the list of commands
        |8. EXIT               quits the game
    """.trimMargin().also(::println)


    while (true) {
        when(val command = readLine()!!.trim().uppercase()) {
            "EXIT" -> {
                println("Goodbye!")
                return
            }
            "HELP" -> println(commands)
            else -> tableTop.processCommand(command)
        }
    }
}