package edu.uw.cs.lil.tiny.explat;

public interface IResourceRepository {
	<T> T getResource(String id);
}
