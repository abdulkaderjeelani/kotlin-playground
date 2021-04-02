package hello

import arrow.core.Eval
import arrow.core.Id
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.fx
import arrow.core.extensions.option.applicative.applicative
import arrow.core.fix
import arrow.core.getOrElse
import arrow.core.none
import java.util.UUID

fun main(vararg args: String) {

    run {
        val someValue: Option<String> = Some("foo")
        someValue.getOrElse { "bar" }.println
    }

    run {
        val noneValue = None
        noneValue.getOrElse { "bar" }.println
    }

    run {
        val someValue: Option<Double> = Some(20.0)
        val value = when (someValue) {
            is Some -> someValue.value
            None -> 0.0
        }
        value.println
    }

    run {
        Option(1).map { it + 1 }.println
        None.map { it }.println
    }

    run {
        Option.fx {
            val a = Option(1).bind()
            val b = Option(a + 1).bind()
            a + b
        }.fix().println
    }

    run {
        val maybeOne = Option(1)
        val maybeTwo = none<Int>()
        val maybeThree = Option(3)

        Option.fx {
            val one = maybeOne.bind()
            val two = maybeTwo.bind()
            val three = maybeThree.bind()
            one + two + three
        }.fix().println
    }

    run {
        val maybeId = Option(UUID.randomUUID())
        val maybeName = Option("foo bar")
        val maybeAge = Option(123)

        Option.applicative().mapN(maybeId, maybeName, maybeAge) { (id, name, age) ->
            id to name to age
        }.fix().println
    }

    run {
        val id = Id.just(3)
        id.map { it + 3 }.println
    }

    run {
        val evalOne = Eval.now(1)
        val evalTwo = Eval.now(2)

        evalOne.flatMap { one ->
            evalTwo.map { two ->
                one + two
            }
        }.value().println
    }

    run {
        val evalOne = Eval.now(1)
        val evalTwo = Eval.now(2)
        val evalThree = Eval.now(3)

        Eval.fx {
            val one = evalOne.bind()
            val two = evalTwo.bind()
            val three = evalThree.bind()
            one + two + three
        }.fix().value().println
    }
}

inline val <T> Some<T>.value get() = this.t

