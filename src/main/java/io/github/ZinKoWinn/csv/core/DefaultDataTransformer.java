package io.github.zinkowinn.csv.core;


import io.github.zinkowinn.csv.mapper.DataTransformer;

/**
 * Default implementation of DataTransformer interface.
 * This class provides a no-op transformation, returning the data unchanged.
 *
 * @param <T> The type of data being transformed.
 * @author by Zin Ko Winn
 * @since 1.0.0
 */

public class DefaultDataTransformer<T> implements DataTransformer<T> {
    /**
     * Performs a no-op transformation on the input data.
     *
     * @param data The data to be transformed.
     * @return The transformed data, which is the same as the input data.
     */
    @Override
    public T transform(T data) {
        return data;
    }
}
