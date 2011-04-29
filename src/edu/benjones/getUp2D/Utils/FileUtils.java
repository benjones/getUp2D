package edu.benjones.getUp2D.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class FileUtils {

	public static float[] readParameters(String filename) {
		float[] ret = null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line;
			ArrayList<String> lines = new ArrayList<String>(50);
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			ret = new float[lines.size()];
			for (int i = 0; i < lines.size(); ++i) {
				ret[i] = Float.parseFloat(lines.get(i));
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());

		}
		return ret;
	}

	public static void writeParameters(String filename, float[] data) {
		try {
			FileWriter fr = new FileWriter(filename);
			for (float f : data) {
				fr.write(f + "\n");
			}
			fr.close();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}

	}

}
