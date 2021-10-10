import robot.TableTop
import java.io.File

fun main(args: Array<String>) {
    val tableTop = TableTop()

    while (true) {
        val command = readLine()!!.trim()
        if (command == "EXIT") {
            println("Goodbye!")
            return
        }
        tableTop.processCommand(command)
    }
}