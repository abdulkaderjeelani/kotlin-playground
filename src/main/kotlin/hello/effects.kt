package hello

import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.traverse.traverse
import arrow.fx.IO

fun main(vararg args: String) {
    IO<Int> { error("io error") }.attempt().println
    val coll = (1..10).map { if (it % 2 == 0) Either.right(it) else Either.left(it) }
    println(coll)
//    coll.traverse(Either.applicative())

}

