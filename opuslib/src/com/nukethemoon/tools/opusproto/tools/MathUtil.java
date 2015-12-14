package com.nukethemoon.tools.opusproto.tools;

public class MathUtil {

	public static boolean isPointInPolygon (float[] polygon, float pointX, float pointY) {
		if (polygon.length % 2 != 0 || polygon.length == 0) {
			throw new IllegalArgumentException("Invalid polygon.");
		}

		float lastVertexX = polygon[polygon.length - 2];
		float lastVertexY = polygon[polygon.length - 1];

		boolean oddNodes = false;
		for (int i = 0; i < polygon.length; i = i + 2) {
			float tmpVertexX = polygon[i];
			float tmpVertexY = polygon[i + 1];
			if ((tmpVertexY < pointY && lastVertexY >= pointY) || (lastVertexY < pointY && tmpVertexY >= pointY)) {
				if (tmpVertexX + (pointY - tmpVertexY) / (lastVertexY - tmpVertexY) * (lastVertexX - tmpVertexX) < pointX) {
					oddNodes = !oddNodes;
				}
			}
			lastVertexX = tmpVertexX;
			lastVertexY = tmpVertexY;
		}
		return oddNodes;
	}

	/**
	 * Calculates the nearest distance of o point to the edges of a polygon.
	 *
	 * @param polygon A polygon {x0, y0, x1, y1, x2, y2 ...}
	 * @param pointX A point x coordinate
	 * @param pointY A point y coordinate
	 * @param placeholder A placeholder for calculations. Needs to have a length of 2.
	 * @return the nearest distance.
	 */
	static public float nearestDistancePolygonEdgeAndPoint(float[] polygon, float pointX, float pointY, float[] placeholder) {
		if (polygon.length % 2 != 0 || polygon.length == 0) {
			throw new IllegalArgumentException("Invalid polygon.");
		}
		float nearestDistance = -1;
		float tmpDistance;

		for (int i = 0; i <= polygon.length - 2; i = i + 2) {
			float edgePoint01X = polygon[i];
			float edgePoint01Y = polygon[i + 1];
			float edgePoint02X = polygon[(i + 2) % (polygon.length)];
			float edgePoint02Y = polygon[(i + 3) % (polygon.length)];

			tmpDistance = distanceSegmentPoint(edgePoint01X, edgePoint01Y, edgePoint02X, edgePoint02Y, pointX, pointY, placeholder);
			if (tmpDistance < nearestDistance || nearestDistance == -1) {
				nearestDistance = tmpDistance;
			}
		}
		return nearestDistance;
	}


	/** Returns the distance between the given segment and point. */
	public static float distanceSegmentPoint (float startX, float startY, float endX, float endY, float pointX, float pointY, float[] placeholder) {
		nearestSegmentPoint(startX, startY, endX, endY, pointX, pointY, placeholder);
		return dst(placeholder[0], placeholder[1], pointX, pointY);
	}

	/** Returns a point on the segment nearest to the specified point. */
	public static float[] nearestSegmentPoint (float startX, float startY, float endX, float endY, float pointX, float pointY,
											   float[] out) {
		final float xDiff = endX - startX;
		final float yDiff = endY - startY;
		float length2 = xDiff * xDiff + yDiff * yDiff;
		if (length2 == 0) {
			out[0] = startX;
			out[1] = startY;
			return out;
		}
		float t = ((pointX - startX) * (endX - startX) + (pointY - startY) * (endY - startY)) / length2;
		if (t < 0) {
			out[0] = startX;
			out[1] = startY;
			return out;
		}
		if (t > 1) {
			out[0] = endX;
			out[1] = endY;
			return out;
		}
		out[0] = startX + t * (endX - startX);
		out[1] = startY + t * (endY - startY);
		return out;
	}

	public static float dst (float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}


	/** Returns the centroid for the specified non-self-intersecting polygon. */
	static public float[] polygonCentroid (float[] polygon, int offset, int count, float[] outCentroid) {
		if (count < 6) throw new IllegalArgumentException("A polygon must have 3 or more coordinate pairs.");
		float x = 0, y = 0;

		float signedArea = 0;
		int i = offset;
		for (int n = offset + count - 2; i < n; i += 2) {
			float x0 = polygon[i];
			float y0 = polygon[i + 1];
			float x1 = polygon[i + 2];
			float y1 = polygon[i + 3];
			float a = x0 * y1 - x1 * y0;
			signedArea += a;
			x += (x0 + x1) * a;
			y += (y0 + y1) * a;
		}

		float x0 = polygon[i];
		float y0 = polygon[i + 1];
		float x1 = polygon[offset];
		float y1 = polygon[offset + 1];
		float a = x0 * y1 - x1 * y0;
		signedArea += a;
		x += (x0 + x1) * a;
		y += (y0 + y1) * a;

		if (signedArea == 0) {
			outCentroid[0] = 0;
			outCentroid[1] = 0;
		} else {
			signedArea *= 0.5f;
			outCentroid[0] = x / (6 * signedArea);
			outCentroid[1] = y / (6 * signedArea);
		}
		return outCentroid;
	}

	public static float[] scalePolygon(float[] polygon, float scale) {
		for (int i = 0; i < polygon.length; i++) {
			polygon[i] = polygon[i] * scale;
		}
		return polygon;
	}

	public static float[] translatePolygon(float[] polygon, float x, float y) {
		for (int i = 0; i < polygon.length; i = i + 2) {
			polygon[i] = polygon[i] + x;
			polygon[i + 1] = polygon[i + 1] + y;
		}
		return polygon;
	}


}
