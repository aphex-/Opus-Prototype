package com.nukethemoon.tools.opusproto;


public class MathE {


	public static boolean isPointInRect(float x, float y, float recX01, float recY01, float recX02, float recY02) {
		float recLeft = ((recX01 < recX02) ? recX01 : recX02);
		float recRight = ((recX01 > recX02) ? recX01 : recX02);
		float recBottom = ((recY01 < recY02) ? recY01 : recY02);
		float recTop = ((recY01 > recY02) ? recY01 : recY02);
		return (x >= recLeft) && (x < recRight) && (y >= recBottom) && (y < recTop);
	}

}
