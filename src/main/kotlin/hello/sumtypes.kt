package hello

fun main(args: Array<String>) {
    val exp = Binary(
        Constant(2.0),
        Operator.PLUS,
        Binary(
            Constant(3.0),
            Operator.MULTI,
            Constant(4.0)
        )
    )

    println(exp.tree())
    println(exp.evaluate())
}

sealed class Expression

class Constant(val value: Double): Expression()

class Binary(val left: Expression,
             val operator: Operator,
             val right: Expression): Expression()

enum class Operator {
    PLUS,
    MULTI
}

fun Expression.tree(): String = when (this) {
    is Constant -> "$value"
    is Binary -> "($operator ${left.tree()} ${right.tree()})"
}

fun Expression.evaluate(): Double = when (this) {
    is Constant -> value
    is Binary -> when (operator) {
        Operator.PLUS -> left.evaluate() + right.evaluate()
        Operator.MULTI -> left.evaluate() * right.evaluate()
    }
}

