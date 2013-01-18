package edu.uw.cs.utils.codec;

import java.nio.ByteBuffer;

public final class Codecs {
	public final static ICodec<String>	STRING_CODEC	= new StringCodec();
	
	private Codecs() {
		// Utilities only class
	}
	
	static private class StringCodec implements ICodec<String> {
		
		public String decode(ByteBuffer buffer) {
			return CodecUtils.decodeString(buffer);
		}
		
		public void encode(String o, ByteBuffer buffer) {
			CodecUtils.encodeString(o, buffer);
		}
	}
}
