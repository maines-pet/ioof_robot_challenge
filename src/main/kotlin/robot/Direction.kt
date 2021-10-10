package robot

enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun left(): Direction {

        return if (this.name == "NORTH") {
            WEST
        } else {
            values().find { it.ordinal == this.ordinal - 1 }!!
        }
    }

    fun right(): Direction {
        return if (this.name == "WEST") {
            NORTH
        } else {
            values().find { it.ordinal == this.ordinal + 1 }!!
        }
    }
}