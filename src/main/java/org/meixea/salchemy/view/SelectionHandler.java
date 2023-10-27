package org.meixea.salchemy.view;

@FunctionalInterface
public interface SelectionHandler<T> {
    void handle(T item);
}
