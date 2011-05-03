package edu.benjones.getUp2D.Utils;

public class FixLineSearchStep {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String input = "./SPParameters/limits.par";
		String output = "./SPParameters/lineSearchStep.par";
		float[] in = FileUtils.readParameters(input);
		float[] out = new float[in.length];
		for (int i = 0; i < in.length; ++i) {
			out[i] = in[i] * 10;
		}
		FileUtils.writeParameters(output, out);

	}

}
