package pt.neticle.ark.base;

public interface TwoWayRouter extends Router, ReverseRouter
{
    default void precompute () {};
}
