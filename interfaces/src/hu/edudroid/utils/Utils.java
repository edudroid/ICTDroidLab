package hu.edudroid.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Utils {

	/**
	 * Create a byte array containing an integer for the length of the map and than the keys and values one pair after another
	 * @param map
	 * @return
	 */
	public static byte[] mapToByteArray(Map<String, Object> map) {
		try{
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(bytes);
			if (map != null) {
				stream.writeObject(Integer.valueOf(map.size()));
				for (String key : map.keySet()) {
					stream.writeObject(key);
					stream.writeObject(map.get(key));
				}
				byte[] parameters = bytes.toByteArray();
				return parameters;
			}			
			bytes.close();
			stream.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String, Object> byteArrayToMap(byte[] bytes) throws IOException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (bytes != null) {
				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bis);
				Integer paramsCount = (Integer) ois.readObject();
				for (int i = 0; i < paramsCount; i++) {
					try {
						// Read key and value from intent, should both be present
						map.put((String)ois.readObject(), ois.readObject());
					} catch (Exception e){
						throw new IOException("Couldn't parse map from bytes ", e);
					}
				}
			}
			return map;
		} catch (Exception e) {
			throw new IOException("Couldn't parse map from bytes ", e);
		}
		
	}
}
