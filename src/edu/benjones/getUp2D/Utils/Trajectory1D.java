package edu.benjones.getUp2D.Utils;

import java.util.TreeSet;

public class Trajectory1D {
	public static class entry implements Comparable<Object> {
		public float t;
		public float val;

		// stupid generics :(
		@Override
		public int compareTo(Object o) {
			return (int) Math.signum((t - ((entry) o).t));
		}

		public entry(float t, float val) {
			this.t = t;
			this.val = val;
		}

		// sets val to null, used for searching
		public entry(float t) {
			this.t = t;
			this.val = 0;
		}
	}

	private TreeSet<entry> data;

	public Trajectory1D(){
		data = new TreeSet<entry>();
	}
	
	public void clear(){
		data.clear();
	}
	
	public void addKnot(entry e) {
		data.add(e);
	}

	public float getMinT() {
		if(data.size() == 0)
			return Float.NEGATIVE_INFINITY;
		return data.first().t;
	}

	public float getMaxT() {
		if(data.size() == 0)
			return Float.POSITIVE_INFINITY;
		return data.last().t;
	}

	public float evaluateLinear(float t) {
		entry before = data.floor(new entry(t));
		entry after = data.ceiling(new entry(t));

		if (before == null) {
			if (after == null) {
				System.out.println("warning: evaluated empty trajectory");
				return 0;// this probably makes sense
			} else {
				return after.val;
			}
		} else {
			if (after == null)
				return before.val;
			if (MathUtils.floatEquals(before.t, t))
				return before.val;
			if(MathUtils.floatEquals(after.t, t))
				return after.val;
			float a = before.val;
			float b = after.val;
			return (a + (b - a) * t / (after.t - before.t));
		}
	}

}
