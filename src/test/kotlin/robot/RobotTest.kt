package robot

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RobotTest {

    @Test
    fun `Robot should face to the left`() {
        val robot = Robot(0,0, 0, Direction.EAST).also {
            it.faceLeft()
            assertEquals(Direction.NORTH, it.face)
        }
        robot.faceLeft()
        assertEquals(Direction.WEST, robot.face)

        robot.faceLeft()
        assertEquals(Direction.SOUTH, robot.face)

        robot.faceLeft()
        assertEquals(Direction.EAST, robot.face)

        repeat(4) { robot.faceLeft() }
        assertEquals(Direction.EAST, robot.face)
    }

    @Test
    fun `Robot should face to the right`() {
        val robot = Robot(0,0, 0).also {
            it.faceRight()
            assertEquals(Direction.SOUTH, it.face)
        }
        robot.faceRight()
        assertEquals(Direction.WEST, robot.face)

        robot.faceRight()
        assertEquals(Direction.NORTH, robot.face)

        robot.faceRight()
        assertEquals(Direction.EAST, robot.face)

        repeat(4) { robot.faceRight() }
        assertEquals(Direction.EAST, robot.face)
    }

    @Test
    fun `Robot should move to the right position`() {
        val robot = Robot(xPos = 0, yPos = 0, face = Direction.NORTH)
        robot.move()
        assertEquals(0, robot.xPos)
        assertEquals(1, robot.yPos)
        assertEquals(Direction.NORTH, robot.face)

        robot.faceLeft()
        robot.move()
        assertEquals(-1, robot.xPos)
        assertEquals(1, robot.yPos)
        assertEquals(Direction.WEST, robot.face)

        robot.faceLeft()
        robot.move()
        assertEquals(-1, robot.xPos)
        assertEquals(0, robot.yPos)
        assertEquals(Direction.SOUTH, robot.face)

        robot.faceLeft()
        robot.move()
        assertEquals(0, robot.xPos)
        assertEquals(0, robot.yPos)
        assertEquals(Direction.EAST, robot.face)

        robot.faceLeft()
        repeat(4) {
            robot.move()
        }
        assertEquals(0, robot.xPos)
        assertEquals(4, robot.yPos)
        assertEquals(Direction.NORTH, robot.face)

        robot.faceRight()
        repeat(8) {
            robot.move()
        }
        assertEquals(8, robot.xPos)
        assertEquals(4, robot.yPos)
        assertEquals(Direction.EAST, robot.face)

    }


}