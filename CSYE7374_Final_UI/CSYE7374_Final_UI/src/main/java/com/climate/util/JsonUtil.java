package com.climate.util;

//import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * A util class for serialize/deserialize between an object and a json file.
 *
 */
public class JsonUtil {
	private static ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Serialize an object to a string.
	 *
	 * @param o
	 * @return
	 * @throws IOException
	 */
	public static String serialize(Object o) throws IOException {
		return objectMapper.writeValueAsString(o);
	}

	/**
	 * Deserialize a string to an Object.
	 *
	 * @param s
	 * @param T
	 * @return
	 * @throws IOException
	 */
	public static Object deserialize(String s, Class<?> T) throws IOException {
		return objectMapper.readValue(s, T);

	}

	/**
	 * Deserialize a byte array to an Object.
	 *
	 * @param byteArray
	 * @param T
	 * @return
	 * @throws IOException
	 */
	public static Object deserialize(byte[] byteArray, Class<?> T) throws IOException {
		return objectMapper.readValue(byteArray, T);
	}
}