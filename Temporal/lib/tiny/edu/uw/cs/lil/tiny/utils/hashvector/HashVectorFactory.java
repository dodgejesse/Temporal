package edu.uw.cs.lil.tiny.utils.hashvector;

public class HashVectorFactory {
	public static Type	DEFAULT	= Type.TREE;
	
	public static IHashVector create() {
		switch (DEFAULT) {
			case TREE:
				return createTree();
			case TROVE:
				return createTrove();
			default:
				throw new IllegalStateException("unhandled type");
		}
	}
	
	public static IHashVector create(IHashVectorImmutable vector) {
		switch (DEFAULT) {
			case TREE:
				return createTree(vector);
			case TROVE:
				return createTrove(vector);
			default:
				throw new IllegalStateException("unhandled type");
		}
	}
	
	public static IHashVector createTree() {
		return new TreeHashVector();
	}
	
	public static IHashVector createTree(IHashVectorImmutable vector) {
		return new TreeHashVector(vector);
	}
	
	public static IHashVector createTrove() {
		return new TroveHashVector();
	}
	
	public static IHashVector createTrove(IHashVectorImmutable vector) {
		return new TroveHashVector(vector);
	}
	
	public static enum Type {
		TREE, TROVE;
	}
	
}
