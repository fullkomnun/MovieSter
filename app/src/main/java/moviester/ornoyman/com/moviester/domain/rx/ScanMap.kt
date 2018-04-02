package moviester.ornoyman.com.moviester.domain.rx

import android.support.annotation.CheckResult
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.functions.BiFunction

/**
 * Similar to [Observable.scan] but the accumulator item will be an
 * [<] rather than a [T].
 * Each emitted stream will be concatenated into the flattened result stream.
 * The accumulator function will be invoked for each source emitted item and last item emitted
 * from accumulator stream
 * and it's result stream will be used in the next accumulator call.
 *
 * This allows emitting multiple results of each iteration into result stream, while feeding the
 * last one into the feedback loop for next iteration.
 */
@CheckResult
@CheckReturnValue
fun <T : Any, R : Any> Observable<T>.scanMap(initialValue: Observable<R>, accumulator: BiFunction<R, in T, Observable<R>>): Observable<R> =
        scan(initialValue,
                { o: Observable<R>, x: T -> o.concatMap({ y -> accumulator.apply(y, x) }).replay(1).autoConnect() })
                .concatMap({ o -> o })