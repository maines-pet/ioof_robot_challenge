package robot

class Robot(val id: Int = 1, var xPos: Int = 0, var yPos: Int = 0, var face: Direction = Direction.EAST) {

    fun faceLeft() {
        face = face.left()
    }

    fun faceRight() {
        face = face.right()
    }

    fun move(positionValidator: ((x: Int, y: Int, id: Int) -> Boolean)? = null) {
        var x = this.xPos
        var y = this.yPos
        when (face) {
            Direction.NORTH -> y++
            Direction.SOUTH -> y--
            Direction.EAST -> x++
            Direction.WEST -> x--
        }
        if (positionValidator == null || positionValidator(x, y, this.id)) {
            this.xPos = x
            this.yPos = y
        }
    }
    fun report() = """ROBOT $id @ $xPos,$yPos, Facing $face"""

    override fun toString(): String {
        return """ROBOT $id"""
    }

}
