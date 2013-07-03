package edu.uw.cs.utils.filter;

public interface IFilter<E> {
	boolean isValid(E e);
}