package hello

import arrow.optics.Lens

fun main(vararg args: String) {
    val fooValue: Lens<Foo, Int> = Lens(
        get = { foo -> foo.value },
        set = { foo, value -> foo.copy(value = value) }
    )

    val foo = Foo(5)
    fooValue.get(foo).println
    fooValue.set(foo, 10).println
    fooValue.get(fooValue.set(foo, 10)).println
    fooValue.modify(foo) { it + 1 }.println

    val fooValueLift = fooValue.lift { it + 1 }
    fooValueLift(foo).println
}

data class Foo(val value: Int)

