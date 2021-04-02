package hello

class RoverInstructions {
    val left = "left"
    val right = "right"
    val forward = "forward"
    val backward = "backward"
}

class Rover() {

    private val list = mutableListOf<String>()

    infix fun turns(turns: String): Rover {
        list.add("turns $turns")
        return this
    }

    infix fun moves(moves: String): Rover {
        list.add("moves $moves")
        return this
    }

    infix fun stops(f: Rover.() -> Unit) = this.f()

    infix fun publish(f: (String) -> Unit) = f(position)

    private val position: String get() = list.reduce { acc, value -> "$acc $value" }
}

fun runs(f: RoverInstructions.(Rover) -> Rover): Rover = RoverInstructions().f(Rover())

fun toCommandCenter(s: String) = println(s)

fun main() {
    runs {
        it turns left
        it moves forward
        it turns rightx
        it moves backward
    } publish (::toCommandCenter)
}



