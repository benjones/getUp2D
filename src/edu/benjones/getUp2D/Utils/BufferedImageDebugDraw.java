package edu.benjones.getUp2D.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.DebugDraw;

public class BufferedImageDebugDraw extends DebugDraw {

	private Graphics2D g;

	private BufferedImage img;
	private final float scale = 1;

	public BufferedImageDebugDraw() {
		super(new OBBViewportTransform());

		img = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) img.getGraphics();

		viewportTransform.setCamera(img.getWidth() / 2, img.getHeight() / 2,
				scale);
		viewportTransform.setExtents(img.getWidth() / 2, img.getHeight() / 2);
		viewportTransform.setYFlip(true);
	}

	private Polygon setupPolygon(Vec2[] vertices, int vertexCount) {

		int[] xPoints = new int[vertexCount];
		int[] yPoints = new int[vertexCount];

		Vec2 tempPoint = new Vec2();
		for (int i = 0; i < vertexCount; ++i) {
			viewportTransform.getWorldToScreen(vertices[i], tempPoint);
			xPoints[i] = (int) tempPoint.x;
			yPoints[i] = (int) tempPoint.y;
		}
		Polygon ret = new Polygon(xPoints, yPoints, vertexCount);
		return ret;
	}

	@Override
	public void drawPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		Polygon p = setupPolygon(vertices, vertexCount);
		g.setColor(convertColor(color));
		g.draw(p);

	}

	@Override
	public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		Polygon p = setupPolygon(vertices, vertexCount);
		g.setColor(convertColor(color));
		g.fill(p);

	}

	private Ellipse2D.Float circle;
	private Vec2 circleCenter;
	private Line2D.Float circleAxis;

	@Override
	public void drawCircle(Vec2 center, float radius, Color3f color) {
		if (circle == null) {
			circle = new Ellipse2D.Float();
			circleCenter = new Vec2();
			circleAxis = new Line2D.Float();
		}

		circle.height = radius * scale * 2;
		circle.width = circle.height;

		viewportTransform.getWorldToScreen(center, circleCenter);

		circle.x = circleCenter.x - circle.width / 2;
		circle.y = circleCenter.y - circle.height / 2;
		g.setColor(convertColor(color));
		g.draw(circle);
	}

	@Override
	public void drawSolidCircle(Vec2 center, float radius, Vec2 axis,
			Color3f color) {
		if (circle == null) {
			circle = new Ellipse2D.Float();
			circleCenter = new Vec2();
			circleAxis = new Line2D.Float();
		}

		circle.height = radius * scale * 2;
		circle.width = circle.height;

		viewportTransform.getWorldToScreen(center, circleCenter);

		circle.x = circleCenter.x - circle.width / 2;
		circle.y = circleCenter.y - circle.height / 2;
		g.setColor(convertColor(color));

		g.fill(circle);
		if (axis != null) {
			Vec2 axisEnd = new Vec2(center.x + radius * axis.x, center.y
					+ radius * axis.y);

			viewportTransform.getWorldToScreen(axisEnd, axisEnd);
			circleAxis.x1 = circleCenter.x;
			circleAxis.y1 = circleCenter.y;
			circleAxis.x2 = axisEnd.x;
			circleAxis.y2 = axisEnd.y;

			g.setColor(Color.WHITE);
			g.draw(circleAxis);
		}

	}

	@Override
	public void drawPoint(Vec2 position, float f, Color3f color3f) {
		drawSolidCircle(position, 3, null, color3f);
		
	}

	
	private Vec2 segmentP1Global, segmentP2Global;
	private Line2D.Float segmentLine;
	@Override
	public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
		if(segmentP1Global == null){
			segmentP1Global = new Vec2();
			segmentP2Global = new Vec2();
			segmentLine = new Line2D.Float();
		}
		viewportTransform.getWorldToScreen(p1, segmentP1Global);
		viewportTransform.getWorldToScreen(p2, segmentP2Global);
		segmentLine.x1 = segmentP1Global.x;
		segmentLine.y1 = segmentP1Global.y;
		segmentLine.x2 = segmentP2Global.x;
		segmentLine.y2 = segmentP2Global.y;
		
		g.setColor(convertColor(color));
		g.draw(segmentLine);

	}

	@Override
	public void drawXForm(XForm xf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(float x, float y, String s, Color3f color) {
		g.setColor(convertColor(color));
		g.drawString(s, x, y);

	}

	public void saveImage(String filename) {
		try {
			File outputfile = new File(filename);
			ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
			System.out.println("Error writing file: " + e.getMessage());
		}
	}

	private Rectangle2D.Double background;

	public void clear(Color3f color) {
		if (background == null) {
			background = new Rectangle2D.Double(0, 0, img.getWidth(),
					img.getHeight());
		}
		g.setColor(convertColor(color));
		g.fill(background);
	}

	public void clear() {
		clear(Color3f.BLACK);
	}

	private Color convertColor(Color3f c) {
		return new Color((int) c.x, (int) c.y, (int) c.z);
	}

}
