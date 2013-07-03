package edu.uw.cs.utils.codec;

import java.nio.ByteBuffer;

public interface ICodec<T> {
	T decode(ByteBuffer buffer);
	
	void encode(T o, ByteBuffer buffer);
}
