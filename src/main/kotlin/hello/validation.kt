package hello

import arrow.Kind
import arrow.core.*
import arrow.typeclasses.ApplicativeError
import arrow.core.extensions.validated.applicativeError.applicativeError
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.extensions.either.bifunctor.bimap
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.bitraverse.bimap

typealias ValidationError = String

sealed class ValidationStrategy<F>(A: ApplicativeError<F, Nel<ValidationError>>) :
    ApplicativeError<F, Nel<ValidationError>> by A {

    fun <T> using(validator: () -> Kind<F, T>): Kind<F, T> = validator()

    private fun <T> nel(error: ValidationError): Kind<F, T> = raiseError(error.nel())

    fun <T> test(
        t: T,
        validator: (
            t: T,
            pass: (c: T) -> Kind<F, T>,
            fail: (e: ValidationError) -> Kind<F, T>
        ) -> Kind<F, T>
    ): Kind<F, T> = validator(t, ::just, ::nel)

    fun <T> test(
        t: T,
        predicate: (t: T) -> Boolean,
        error: ValidationError
    ) =
        if (predicate(t)) just(t)
        else nel(error)

    object ErrorAccumulationStrategy :
        ValidationStrategy<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))

    object FailFastStrategy :
        ValidationStrategy<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())

    companion object {
        infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
        infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
    }
}


data class Customer(val name: String, val age: Int)

fun <F> nameValidator(
    customer: Customer,
    pass: (c: Customer) -> Kind<F, Customer>,
    fail: (e: ValidationError) -> Kind<F, Customer>
): Kind<F, Customer> =
    if (customer.name.length > 5) pass(customer)
    else fail("name is invalid")

fun <F> ageValidator(
    customer: Customer,
    pass: (c: Customer) -> Kind<F, Customer>,
    fail: (e: ValidationError) -> Kind<F, Customer>
): Kind<F, Customer> =
    if (customer.age in 11..49) pass(customer)
    else fail("age is invalid")


fun main() {

    val worseCustomer = Customer("cust", 60)

    //using lambda
    val accumulated = ValidationStrategy accumulateErrors {
        using {
            mapN(
                test(worseCustomer, { it.name.length > 5 }, "name is invalid"),
                test(worseCustomer, { it.age in 11..49 }, "age is invalid")
            ) { (nameC, ageC) ->
                Customer(nameC.name, ageC.age)
            }
        }
    }
    accumulated.bimap({ valErrors -> valErrors.map(::println) }, ::println)


    //using function reference
    val failedFast = ValidationStrategy failFast {
        using {
            mapN(
                test(worseCustomer, ::nameValidator),
                test(worseCustomer, ::ageValidator)
            ) { (nameC, ageC) ->
                Customer(nameC.name, ageC.age)
            }
        }
    }

    failedFast.bimap({ valErrors -> valErrors.map(::println) }, ::println)

}
