package edu.uw.cs.utils.codec;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CodecUtils {
	
	private CodecUtils() {
		// Utilities only class
	}
	
	public static boolean decodeBoolean(ByteBuffer buffer) {
		return buffer.get() != 0;
	}
	
	public static <T> List<T> decodeList(ByteBuffer buffer, ICodec<T> codec) {
		final int length = buffer.getInt();
		final List<T> list = new ArrayList<T>(length);
		for (int i = 0; i < length; ++i) {
			list.add(codec.decode(buffer));
		}
		return list;
	}
	
	public static <K, V> Map<K, V> decodeMap(ByteBuffer buffer,
			ICodec<K> keyCodec, ICodec<V> valueCodec) {
		final int size = buffer.getInt();
		final Map<K, V> map = new HashMap<K, V>(size);
		
		for (int i = 0; i < size; ++i) {
			final K key = keyCodec.decode(buffer);
			final V value = valueCodec.decode(buffer);
			map.put(key, value);
		}
		
		return map;
	}
	
	public static String decodeString(ByteBuffer buffer) {
		try {
			final int length = buffer.getInt();
			final byte[] bytes = new byte[length];
			buffer.get(bytes);
			return new String(bytes, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
		
	}
	
	public static URL decodeURL(ByteBuffer buffer) {
		try {
			final String protocol = decodeString(buffer);
			final String host = decodeString(buffer);
			final String file = decodeString(buffer);
			return new URL(protocol, host, file);
		} catch (final MalformedURLException e) {
			return null;
		}
	}
	
	public static void encodeBoolean(boolean b, ByteBuffer buffer) {
		buffer.put(b ? (byte) 1 : (byte) 0);
	}
	
	public static <T> void encodeList(List<T> list, ByteBuffer buffer,
			ICodec<T> codec) {
		buffer.putInt(list.size());
		for (final T item : list) {
			codec.encode(item, buffer);
		}
	}
	
	public static <K, V> void encodeMap(Map<K, V> map, ByteBuffer buffer,
			ICodec<K> keyCodec, ICodec<V> valueCodec) {
		buffer.putInt(map.size());
		for (final Map.Entry<K, V> entry : map.entrySet()) {
			keyCodec.encode(entry.getKey(), buffer);
			valueCodec.encode(entry.getValue(), buffer);
		}
	}
	
	public static void encodeString(String s, ByteBuffer buffer) {
		try {
			final byte[] bytes = s.getBytes("UTF-8");
			buffer.putInt(bytes.length);
			buffer.put(bytes);
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static void encodeURL(URL url, ByteBuffer buffer) {
		encodeString(url.getProtocol(), buffer);
		encodeString(url.getHost(), buffer);
		encodeString(url.getFile(), buffer);
	}
}
