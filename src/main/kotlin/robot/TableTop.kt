package robot

import java.lang.IllegalArgumentException

class TableTop(private val length: Int = 5, private val width: Int = 5) {
    val robots: MutableList<Robot> = mutableListOf()
    var activeRobot: Robot? = null

    fun processCommand(command: String) {

        val commandUppercase = command.uppercase()

        if (commandUppercase.startsWith("PLACE ")) {
            parsePlaceCommand(commandUppercase)?.let {
                val (x, y, f) = it
                val robot = Robot(robots.size + 1, x, y, Direction.valueOf(f))
                if (activeRobot == null) {
                    activeRobot = robot
                }
                robots.add(robot)
            }
            return
        }

        if (activeRobot == null) return //Only process the other commands
                                        // if a PLACE command was successful (ie activeRobot is set)
                                        //, otherwise ignore command.

        if (commandUppercase.startsWith("ROBOT ", true)) {
            parseRobotCommand(commandUppercase)?.let {
                activeRobot = robots.find { robot -> robot.id == it }
                println("""ROBOT $it is now active""")
            }
            return
        }

        when (commandUppercase) {
            "LEFT" -> activeRobot?.faceLeft()
            "RIGHT" -> activeRobot?.faceRight()
            "MOVE" -> activeRobot?.move { x, y, id -> !isOccupied(x, y, id)
                    && isWithinBounds(x, y)}
            "REPORT" -> {
                robots.forEach {
                    println(it.report() + if (activeRobot!! == it) " is Active" else "")
                }
                println("There are a total of ${this.robots.size} robot${if (this.robots.size > 1) "s" else ""} on the table.")
            }
        }

    }

    private fun parsePlaceCommand(place: String): Triple<Int, Int, String>? {
        val find = placeCommandPattern.find(place)
        if (find != null) {
            val (xString, yString, face) = find.destructured
            val x = xString.toInt()
            val y = yString.toInt()
            return if (isValidPlaceCommand(x, y, face)) {
                Triple(x, y, face)
            } else {
                null
            }
        }
        return null
    }

    private fun parseRobotCommand(command: String): Int? {
        return command.replace("ROBOT ", "").toInt()
    }

    private fun isValidPlaceCommand(x: Int,y: Int, face: String): Boolean {
        return isWithinBounds(x, y)
                && !isOccupied(x, y)
                && isValidFaceDirection(face)
    }
    private fun isWithinBounds(x: Int, y: Int): Boolean = (x in 0 until width) && (y in 0 until length)
    private fun isOccupied(x: Int, y: Int): Boolean = robots.any { it.xPos == x && it.yPos == y }
    private fun isOccupied(x: Int, y: Int, robotToExclude: Int) = robots.filter {it.id != robotToExclude }
        .any { it.xPos == x && it.yPos == y }
    private fun isValidFaceDirection(face: String): Boolean {
        try {
            Direction.valueOf(face)
        } catch (e: IllegalArgumentException) {
            return false
        }
        return true
    }
}

val placeCommandPattern = Regex("""PLACE (\d+),(\d+),(\w+)""")