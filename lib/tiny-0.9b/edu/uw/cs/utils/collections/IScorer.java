package edu.uw.cs.utils.collections;

public interface IScorer<E> {
	double score(E e);
}