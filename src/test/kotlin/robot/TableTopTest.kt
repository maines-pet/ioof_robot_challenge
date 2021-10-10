package robot

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.ByteArrayOutputStream
import java.io.PrintStream


internal class TableTopTest {
    private val standardOut = System.out
    private val outputStreamCaptor = ByteArrayOutputStream()

    @BeforeEach
    fun setUp() {
        System.setOut(PrintStream(outputStreamCaptor))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(standardOut)
    }


    @Test
    fun `Sample a`() {
        val command = """PLACE 0,0,NORTH
                    |MOVE
                    |REPORT""".trimMargin().lines()
        val tableTop = TableTop()

        command.forEach (tableTop::processCommand)

        val expected = """ROBOT 1 @ 0,1, Facing NORTH is Active
            |There are a total of 1 robot on the table.""".trimMargin().normaliseNewLine()
        val actual = outputStreamCaptor.toString().trim().normaliseNewLine()

        assertEquals(expected, actual)
    }

    @Test
    fun `Sample b`() {
        val command = """PLACE 0,0,NORTH
            |LEFT
            |REPORT""".trimMargin().lines()
        val tableTop = TableTop()

        command.forEach (tableTop::processCommand)

        val expected = """ROBOT 1 @ 0,0, Facing WEST is Active
            |There are a total of 1 robot on the table.""".trimMargin().normaliseNewLine()
        val actual = outputStreamCaptor.toString().trim().normaliseNewLine()

        assertEquals(expected, actual)
    }

    @Test
    fun `Sample c`() {
        val command = """PLACE 1,2,EAST
                |MOVE
                |MOVE
                |LEFT
                |MOVE
                |REPORT""".trimMargin().lines()
        val tableTop = TableTop()

        command.forEach (tableTop::processCommand)

        val expected = """ROBOT 1 @ 3,3, Facing NORTH is Active
            |There are a total of 1 robot on the table.""".trimMargin().normaliseNewLine()
        val actual = outputStreamCaptor.toString().trim().normaliseNewLine()

        assertEquals(expected, actual)
    }

    @Test
    fun `PLACE should create a new Robot`() {
        val command = "PLACE 1,2,NORTH"
        val table = TableTop(5, 5)
        table.processCommand(command)
        assertEquals("ROBOT 1", table.activeRobot.toString())
        assertEquals(1, table.activeRobot?.xPos)
        assertEquals(2, table.activeRobot?.yPos)
    }

    @Test
    fun `No Active Robots returned when table top is empty`() {
        val table = TableTop(5, 5)
        assertTrue(table.robots.isEmpty())
        assertNull(table.activeRobot)
    }

    @Test
    fun `Multiple PLACE commands create new robots`() {
        val table = TableTop(5, 5)
        table.processCommand("PLACE 0,0,NORTH")
        table.processCommand("PLACE 3,4,SOUTH")
        table.processCommand("PLACE 4,4,EAST")
        table.processCommand("PLACE 1,2,WEST")
        assertEquals("ROBOT 1", table.activeRobot.toString())

        val robot1 = table.robots.find { it.id == 1 }!!
        assertEquals(0, robot1.xPos)
        assertEquals(0, robot1.yPos)
        assertEquals(Direction.NORTH, robot1.face)

        val robot2 = table.robots.find { it.id == 2 }!!
        assertEquals(3, robot2.xPos)
        assertEquals(4, robot2.yPos)
        assertEquals(Direction.SOUTH, robot2.face)

        val robot3 = table.robots.find { it.id == 3 }!!
        assertEquals(4, robot3.xPos)
        assertEquals(4, robot3.yPos)
        assertEquals(Direction.EAST, robot3.face)

        val robot4 = table.robots.find { it.id == 4 }!!
        assertEquals(1, robot4.xPos)
        assertEquals(2, robot4.yPos)
        assertEquals(Direction.WEST, robot4.face)

    }

    @ParameterizedTest
    @ValueSource(strings = ["PLACE -1,-1,NORTH", "PLACE 5,5,SOUTH", "PLACE 0,6,EAST", "PLACE 7,4,WEST"])
    fun `PLACE command outside the bounds are ignored`(command: String){
        val tableTop = TableTop(5, 5)
        tableTop.processCommand(command)
        assertNull(tableTop.activeRobot)
        assertEquals(0, tableTop.robots.size)
    }

    @ParameterizedTest
    @ValueSource(strings = ["PLACE 0,0,NORT", "place 1,1,sou", "place 3,3,test", "place 3,3,"])
    fun `PLACE command with invalid directions are ignored`(command: String){
        val tableTop = TableTop(5, 5)
        tableTop.processCommand(command)
        assertNull(tableTop.activeRobot)
        assertEquals(0, tableTop.robots.size)
    }

    @Test
    fun `Ignore commands until first PLACE command is encountered`() {
        val commands = listOf("MOVE", "REPORT", "LEFT", "RIGHT")
        val tableTop = TableTop(5, 5)
        commands.forEach { tableTop.processCommand(it) }
        assertNull(tableTop.activeRobot)
        assertEquals(0, tableTop.robots.size)

        val commandsWithPlace = listOf("ROBOT 1", "MOVE", "REPORT", "LEFT", "RIGHT",
            //valid commands starts here
            "PLACE 1,1,NORTH","MOVE","RIGHT","MOVE","MOVE"
            )
        commandsWithPlace.forEach { tableTop.processCommand(it) }
        assertNotNull(tableTop.activeRobot)
        assertEquals("ROBOT 1", tableTop.activeRobot.toString())
        assertEquals(3, tableTop.activeRobot?.xPos)
        assertEquals(2, tableTop.activeRobot?.yPos)
    }

    @Test
    fun `Command that places a robot on an occupied tile will be ignored`() {
        val commands = listOf("PLACE 3,2,NORTH","PLACE 3,2,SOUTH","PLACE 3,2,NORTH","PLACE 2,4,EAST")
        val tableTop = TableTop(5, 5)
        commands.forEach(tableTop::processCommand)
        assertNotNull(tableTop.activeRobot)
        assertEquals(2, tableTop.robots.size)
        assertEquals("ROBOT 1", tableTop.activeRobot.toString())
        assertEquals(3, tableTop.activeRobot?.xPos)
        assertEquals(2, tableTop.activeRobot?.yPos)
        assertEquals(Direction.NORTH, tableTop.activeRobot?.face)
        with(tableTop.robots[0]) {
            assertEquals("ROBOT 1", toString())
            assertEquals(3, this.xPos)
            assertEquals(2, this.yPos)
            assertEquals(Direction.NORTH, this.face)
        }
        with(tableTop.robots[1]) {
            assertEquals("ROBOT 2", toString())
            assertEquals(2, this.xPos)
            assertEquals(4, this.yPos)
            assertEquals(Direction.EAST, this.face)
        }
    }

    @Test
    fun `MOVE commands that will result to a robot falling off the table will be ignored`() {
        val tableTop = TableTop(5, 5)
        tableTop.processCommand("PLACE 4,4,NORTH")
        tableTop.processCommand("MOVE")
        assertEquals(4, tableTop.activeRobot?.xPos)
        assertEquals(4, tableTop.activeRobot?.yPos)
        assertEquals(Direction.NORTH, tableTop.activeRobot?.face)

        tableTop.processCommand("RIGHT")
        tableTop.processCommand("MOVE")
        assertEquals(4, tableTop.activeRobot?.xPos)
        assertEquals(4, tableTop.activeRobot?.yPos)
        assertEquals(Direction.EAST, tableTop.activeRobot?.face)

        tableTop.processCommand("RIGHT")
        tableTop.processCommand("MOVE")
        assertEquals(4, tableTop.activeRobot?.xPos)
        assertEquals(3, tableTop.activeRobot?.yPos)
        assertEquals(Direction.SOUTH, tableTop.activeRobot?.face)

    }

    @Test
    fun `MOVE commands that will result to collision with other robot are ignored`() {
        val tableTop = TableTop(5, 5)
        tableTop.processCommand("PLACE 4,4,SOUTH")
        tableTop.processCommand("PLACE 4,3,NORTH")

        tableTop.processCommand("ROBOT 2")
        tableTop.processCommand("MOVE")

        assertEquals(4, tableTop.activeRobot?.xPos)
        assertEquals(3, tableTop.activeRobot?.yPos)
        assertEquals(Direction.NORTH, tableTop.activeRobot?.face)

        tableTop.processCommand("ROBOT 1")
        tableTop.processCommand("MOVE")

        assertEquals(4, tableTop.activeRobot?.xPos)
        assertEquals(4, tableTop.activeRobot?.yPos)
        assertEquals(Direction.SOUTH, tableTop.activeRobot?.face)
    }

    @Test
    fun `Non-active robots should not be affected by the MOVE, LEFT, and RIGHT Command`() {
        val tableTop = TableTop(5, 5)
        tableTop.processCommand("PLACE 4,0,SOUTH")
        tableTop.processCommand("PLACE 4,3,NORTH")

        tableTop.processCommand("ROBOT 2")
        tableTop.processCommand("MOVE")

        assertEquals(4, tableTop.activeRobot?.xPos)
        assertEquals(4, tableTop.activeRobot?.yPos)
        assertEquals(Direction.NORTH, tableTop.activeRobot?.face)

        val robot1 = tableTop.robots.find { it.id == 1}!!
        assertEquals(4, robot1.xPos)
        assertEquals(0, robot1.yPos)
        assertEquals(Direction.SOUTH, robot1.face)
    }

    @Test
    fun `REPORT command should print out the robot positions, active status and total`(){
        val table = TableTop(5, 5)
        table.processCommand("PLACE 0,0,NORTH")
        table.processCommand("PLACE 3,4,SOUTH")
        table.processCommand("PLACE 4,4,EAST")
        table.processCommand("PLACE 1,2,WEST")
        table.processCommand("REPORT")

        val expected = """ROBOT 1 @ 0,0, Facing NORTH is Active
            |ROBOT 2 @ 3,4, Facing SOUTH
            |ROBOT 3 @ 4,4, Facing EAST
            |ROBOT 4 @ 1,2, Facing WEST
            |There are a total of 4 robots on the table.
        """.trimMargin().normaliseNewLine()

        val actual = outputStreamCaptor.toString().trim().normaliseNewLine()
        assertEquals(expected, actual)
    }

    private fun String.normaliseNewLine() : String {
        return this.replace("(\\r\\n|\\r|\\n)".toRegex(), "")
    }

}