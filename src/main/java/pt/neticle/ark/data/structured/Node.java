package pt.neticle.ark.data.structured;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A node within a data structure
 */
public interface Node
{
    /**
     * Represents a list of nodes in a data structure
     */
    interface List extends Node, Iterable<Node>
    {
        /**
         * Gets the number of entries in this list
         * @return
         */
        int count();

        /**
         * Gets the node at the specified index.
         *
         * @throws IndexOutOfBoundsException
         *
         * @param i index
         * @return
         */
        Node get(int i);

        /**
         * Gets a stream of this list's entries
         * @return
         */
        Stream<Node> stream();
    }

    /**
     * Represents an associative list of nodes, arranged as key => value pairs.
     */
    interface Associative extends Node
    {
        /**
         * Gets the number of entries in this node
         *
         * @return
         */
        int count();

        /**
         * Gets the node associated with the specified key
         *
         * @param key
         * @return The node if present, null otherwise.
         */
        Node get(String key);

        /**
         * Gets a set containing all entries of this structure
         * @return
         */
        Set<Map.Entry<String, Node>> entrySet();

        /**
         * Gets a streams of all entries of this structure
         * @return
         */
        Stream<Map.Entry<String, Node>> streamEntries();

        /**
         * Gets a stream of all keys of this structure
         * @return
         */
        Stream<String> keys();

        /**
         * Gets a stream of all values of this structure
         * @return
         */
        Stream<Node> values();
    }

    /**
     * Represents a container node for a single value
     *
     * @param <TValue>
     */
    @FunctionalInterface
    interface Literal<TValue> extends Node
    {
        TValue get();
    }
}
