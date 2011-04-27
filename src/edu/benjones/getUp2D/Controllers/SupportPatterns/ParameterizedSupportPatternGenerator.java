package edu.benjones.getUp2D.Controllers.SupportPatterns;

import edu.benjones.getUp2D.Controllers.SupportPattern;

public interface ParameterizedSupportPatternGenerator {
	public SupportPattern getPattern(float[] parameters);
}
