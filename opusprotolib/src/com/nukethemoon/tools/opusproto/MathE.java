package com.nukethemoon.tools.opusproto;



/**
 * @author LucaHofmann@gmx.net
 */
public class MathE {

	/**
	 * Documentation.
	 * todo : add documentation
	 * @param v_12 add documentation
	 * @param v02 add documentation
	 * @param v12 add documentation
	 * @param v22 add documentation
	 * @param v_11 add documentation
	 * @param v01 add documentation
	 * @param v11 add documentation
	 * @param v21 add documentation
	 * @param v_10 add documentation
	 * @param v00 add documentation
	 * @param v10 add documentation
	 * @param v20 add documentation
	 * @param v_1_1 add documentation
	 * @param v0_1 add documentation
	 * @param v1_1 add documentation
	 * @param v2_1 add documentation
	 * @param x add documentation
	 * @param y add documentation
	 * @return The interpolated value.
	 */
	public static float getCubicInterpolation2d(float v_12, 	float v02, 	float v12, 	float v22,
												float v_11, 	float v01, 	float v11, 	float v21,
												float v_10, 	float v00, 	float v10, 	float v20,
												float v_1_1, 	float v0_1,	float v1_1,	float v2_1,
												float x, 		float y){

		float x_1 = getCubicInterpolation1D(v_1_1, v_10, v_11, v_12, y);
		float x0 = getCubicInterpolation1D(v0_1, v00, v01, v02, y);
		float x1 = getCubicInterpolation1D(v1_1, v10, v11, v12, y);
		float x2 = getCubicInterpolation1D(v2_1, v20, v21, v22, y);
		return getCubicInterpolation1D(x_1, x0, x1, x2, x);
	}

	/**
	 * Documentation.
	 * todo : add documentation
	 * @param v0 add documentation
	 * @param v1 add documentation
	 * @param v2 add documentation
	 * @param v3 add documentation
	 * @param x add documentation
	 * @return add documentation
	 */
	public static float getCubicInterpolation1D(float v0, float v1, float v2, float v3, float x) {
		return(((v3 - v2) - (v0 - v1)) * x * x * x + ((v0 - v1) - ((v3 - v2) - (v0 - v1))) * x * x + (v2 - v0) * x + v1);
	}


	/**
	 * Returns true if a point is inside a circle.
	 * @param pointX The x position of the point.
	 * @param pointY The y position of the point.
	 * @param circleX The x position of the circle.
	 * @param circleY The y position of the circle.
	 * @param circleRadius The radius of the circle.
	 * @return True if a point is inside a circle.
	 */
	public static boolean isPointInCircle(float pointX, float pointY, float circleX, float circleY, float circleRadius) {
		return Math.pow((pointX - circleX), 2d) + Math.pow((pointY - circleY), 2d) < Math.pow(circleRadius, 2);
	}

	/**
	 *
	 * @param pointX
	 * @param pointY
	 * @param rectangleCenterX
	 * @param rectangleCenterY
	 * @param rectangleWidth
	 * @param rectangleHeight
	 * @return
	 */
	public static boolean isPointInCenteredRect(float pointX, float pointY, float rectangleCenterX, float rectangleCenterY, float rectangleWidth, float rectangleHeight) {
		return (rectangleCenterX - rectangleWidth / 2f) <= pointX
			&& (rectangleCenterX + rectangleWidth / 2f) >= pointX
			&& (rectangleCenterY - rectangleHeight / 2f) <= pointY
			&& (rectangleCenterY + rectangleHeight / 2f) >= pointY;
	}


	public static boolean isPointInRect(float x, float y, float recX01, float recY01, float recX02, float recY02) {
		float recLeft = ((recX01 < recX02) ? recX01 : recX02);
		float recRight = ((recX01 > recX02) ? recX01 : recX02);
		float recBottom = ((recY01 < recY02) ? recY01 : recY02);
		float recTop = ((recY01 > recY02) ? recY01 : recY02);
		return (x >= recLeft) && (x < recRight) && (y >= recBottom) && (y < recTop);
	}

}
