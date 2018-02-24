package pt.neticle.ark.functional;

@FunctionalInterface
public interface ThrowingSupplier<TResult, TException extends Throwable>
{
    TResult get () throws TException;
}
