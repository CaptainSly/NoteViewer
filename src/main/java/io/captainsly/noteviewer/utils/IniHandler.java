package io.captainsly.noteviewer.utils;

import java.io.File;

import org.ini4j.Ini;

public class IniHandler {

	private static final File FILE = new File("config.ini");

	public static void writekey(String section, String key, String value) throws Exception {
		Ini ini = new Ini(FILE);
		ini.put(section, "s" + key, value);
		ini.store();
	}

	public static void writekey(String section, String key, int value) throws Exception {
		Ini ini = new Ini(FILE);
		ini.put(section, "i" + key, value);
		ini.store();
	}

	public static void writekey(String section, String key, float value) throws Exception {
		Ini ini = new Ini(FILE);
		ini.put(section, "f" + key, value);
		ini.store();
	}

	public static void writekey(String section, String key, boolean value) throws Exception {
		Ini ini = new Ini(FILE);
		ini.put(section, "b" + key, value);
		ini.store();
	}

	public static String readKeyString(String section, String key) throws Exception {
		Ini ini = new Ini(FILE);
		String d = ini.get(section, "s" + key, String.class);
		return d;
	}

	public static int readKeyInt(String section, String key) throws Exception {
		Ini ini = new Ini(FILE);
		int d = ini.get(section, "i" + key, int.class);
		return d;
	}

	public static boolean readKeyBool(String section, String key) throws Exception {
		Ini ini = new Ini(FILE);
		int d = ini.get(section, "s" + key, int.class);
		boolean b;
		if (d == 1) b = true;
		else b = false;
		return b;
	}

	public static float readKeyFloat(String section, String key) throws Exception {
		Ini ini = new Ini(FILE);
		float d = ini.get(section, "f" + key, float.class);
		return d;
	}
}
