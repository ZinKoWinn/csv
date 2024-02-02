package io.github.zinkowinn.csv.mapper;

/**
 * @author by Zin Ko Winn
 */

@FunctionalInterface
public interface DataTransformer<T> {
    T transform(T data);
}
