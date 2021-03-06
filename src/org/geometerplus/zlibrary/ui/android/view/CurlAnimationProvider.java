/*
 * Copyright (C) 2007-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.view;

import android.graphics.*;
import android.util.FloatMath;

import org.geometerplus.zlibrary.core.view.ZLView;

class CurlAnimationProvider extends AnimationProvider {
	private final Paint myBackPaint = new Paint();
	private final Paint myEdgePaint = new Paint();

	final Path myFgPath = new Path();
	final Path myEdgePath = new Path();

	private float mySpeedFactor;

	CurlAnimationProvider(Paint paint) {
		super(paint);

		myBackPaint.setAntiAlias(true);
		myBackPaint.setAlpha(0x40);

		myEdgePaint.setAntiAlias(true);
		myEdgePaint.setStyle(Paint.Style.FILL);
		myEdgePaint.setShadowLayer(35, 0, 0, 0xC0000000);
	}

	@Override
	protected void drawInternal(Canvas canvas, Bitmap bgBitmap, Bitmap fgBitmap) {
		canvas.drawBitmap(bgBitmap, 0, 0, myPaint);

		final int cornerX = myStartX > myWidth / 2 ? myWidth : 0;
		final int cornerY = myStartY > myHeight / 2 ? myHeight : 0;
		final int oppositeX = Math.abs(myWidth - cornerX);
		final int oppositeY = Math.abs(myHeight - cornerY);
		final int x, y;
		if (myDirection.IsHorizontal) {
			x = myEndX;
			if (getMode().Auto) {
				y = myEndY;
			} else {
				if (cornerY == 0) {
					y = Math.max(1, Math.min(myHeight / 2, myEndY));
				} else {
					y = Math.max(myHeight / 2, Math.min(myHeight - 1, myEndY));
				}
			}
		} else {
			y = myEndY;
			if (getMode().Auto) {
				x = myEndX;
			} else {
				if (cornerX == 0) {
					x = Math.max(1, Math.min(myWidth / 2, myEndX));
				} else {
					x = Math.max(myWidth / 2, Math.min(myWidth - 1, myEndX));
				}
			}
		}
		final int dX = Math.max(1, Math.abs(x - cornerX));
		final int dY = Math.max(1, Math.abs(y - cornerY));

		final int x1 = cornerX == 0
			? (dY * dY / dX + dX) / 2
			: cornerX - (dY * dY / dX + dX) / 2;
		final int y1 = cornerY == 0
			? (dX * dX / dY + dY) / 2
			: cornerY - (dX * dX / dY + dY) / 2;

		float sX, sY;
		{
			float d1 = x - x1;
			float d2 = y - cornerY;
			sX = FloatMath.sqrt(d1 * d1 + d2 * d2) / 2;
			if (cornerX == 0) {
				sX = -sX;
			}
		}
		{
			float d1 = x - cornerX;
			float d2 = y - y1;
			sY = FloatMath.sqrt(d1 * d1 + d2 * d2) / 2;
			if (cornerY == 0) {
				sY = -sY;
			}
		}

		myFgPath.rewind();
		myFgPath.moveTo(x, y);
		myFgPath.lineTo((x + cornerX) / 2, (y + y1) / 2);
		myFgPath.quadTo(cornerX, y1, cornerX, y1 - sY);
		if (Math.abs(y1 - sY - cornerY) < myHeight) {
			myFgPath.lineTo(cornerX, oppositeY);
		}
		myFgPath.lineTo(oppositeX, oppositeY);
		if (Math.abs(x1 - sX - cornerX) < myWidth) {
			myFgPath.lineTo(oppositeX, cornerY);
		}
		myFgPath.lineTo(x1 - sX, cornerY);
		myFgPath.quadTo(x1, cornerY, (x + x1) / 2, (y + cornerY) / 2);
		canvas.drawPath(myFgPath, myEdgePaint);
		canvas.save();
		canvas.clipPath(myFgPath);
		canvas.drawBitmap(fgBitmap, 0, 0, myPaint);
		canvas.restore();
        
		{
			final int w = Math.min(fgBitmap.getWidth(), 7);
			final int h = Math.min(fgBitmap.getHeight(), 7);
			long r = 0, g = 0, b = 0;
			for (int i = 0; i < w; ++i) {
				for (int j = 0; j < h; ++j) {
					int color = fgBitmap.getPixel(i, j);
					r += color & 0xFF0000;
					g += color & 0xFF00;
					b += color & 0xFF;
				}
			}
			r /= w * h;
			g /= w * h;
			b /= w * h;
			r >>= 16;
			g >>= 8;
			myEdgePaint.setColor(Color.rgb((int)(r & 0xFF), (int)(g & 0xFF), (int)(b & 0xFF)));
		}
        
		myEdgePath.rewind();
		myEdgePath.moveTo(x, y);
		myEdgePath.lineTo(
			(x + cornerX) / 2,
			(y + y1) / 2
		);
		myEdgePath.quadTo(
			(x + 3 * cornerX) / 4,
			(y + 3 * y1) / 4,
			(x + 7 * cornerX) / 8,
			(y + 7 * y1 - 2 * sY) / 8
		);
		myEdgePath.lineTo(
			(x + 7 * x1 - 2 * sX) / 8,
			(y + 7 * cornerY) / 8
		);
		myEdgePath.quadTo(
			(x + 3 * x1) / 4,
			(y + 3 * cornerY) / 4,
			(x + x1) / 2,
			(y + cornerY) / 2
		);

		canvas.drawPath(myEdgePath, myEdgePaint);
		canvas.save();
		canvas.clipPath(myEdgePath);
		final Matrix m = new Matrix();
		m.postScale(1, -1);
		m.postTranslate(x - cornerX, y + cornerY);
		final float angle;
		if (cornerY == 0) {
			angle = -180 / 3.1416f * (float)Math.atan2(x - cornerX, y - y1);
		} else {
			angle = 180 - 180 / 3.1416f * (float)Math.atan2(x - cornerX, y - y1);
		}
		m.postRotate(angle, x, y);
		canvas.drawBitmap(fgBitmap, m, myBackPaint);
		canvas.restore();
	}

	@Override
	ZLView.PageIndex getPageToScrollTo() {
		switch (myDirection) {
			case leftToRight:
				return myStartX < myWidth / 2 ? ZLView.PageIndex.next : ZLView.PageIndex.previous;
			case rightToLeft:
				return myStartX < myWidth / 2 ? ZLView.PageIndex.previous : ZLView.PageIndex.next;
			case up:
				return myStartY < myHeight / 2 ? ZLView.PageIndex.previous : ZLView.PageIndex.next;
			case down:
				return myStartY < myHeight / 2 ? ZLView.PageIndex.next : ZLView.PageIndex.previous;
		}
		return ZLView.PageIndex.current;
	}

	@Override
	protected void startAutoScrollingInternal(boolean forward, float startSpeed, ZLView.Direction direction, int w, int h, Integer x, Integer y, int speed) {
		if (x == null || y == null) {
			if (direction.IsHorizontal) {
				x = startSpeed < 0 ? w - 3 : 3;
				y = 1;
			} else {
				x = 1;
				y = startSpeed < 0 ? h  - 3 : 3;
			}
		} else {
			final int cornerX = x > w / 2 ? w : 0;
			final int cornerY = y > h / 2 ? h : 0;
			int deltaX = Math.min(Math.abs(x - cornerX), w / 5);
			int deltaY = Math.min(Math.abs(y - cornerY), h / 5);
			if (direction.IsHorizontal) {
				deltaY = Math.min(deltaY, deltaX / 3);
			} else {
				deltaX = Math.min(deltaX, deltaY / 3);
			}
			x = Math.abs(cornerX - deltaX);
			y = Math.abs(cornerY - deltaY);
		}
		super.startAutoScrollingInternal(forward, startSpeed, direction, w, h, x, y, speed);
		mySpeedFactor = (float)Math.pow(2.0, 0.25 * speed);
		mySpeed *= 1.5;
	}

	@Override
	void doStep() {
		if (!getMode().Auto) {
			return;
		}

		final int speed = (int)Math.abs(mySpeed);
		mySpeed *= mySpeedFactor;

		final int cornerX = myStartX > myWidth / 2 ? myWidth : 0;
		final int cornerY = myStartY > myHeight / 2 ? myHeight : 0;

		final int boundX, boundY;
		if (getMode() == Mode.AutoScrollingForward) {
			boundX = cornerX == 0 ? 2 * myWidth : -myWidth;
			boundY = cornerY == 0 ? 2 * myHeight : -myHeight;
		} else {
			boundX = cornerX;
			boundY = cornerY;
		}

		final int deltaX = Math.abs(myEndX - cornerX);
		final int deltaY = Math.abs(myEndY - cornerY);
		final int speedX, speedY;
		if (deltaX == 0 || deltaY == 0) {
			speedX = speed;
			speedY = speed;
		} else if (deltaX < deltaY) {
			speedX = speed;
			speedY = speed * deltaY / deltaX;
		} else {
			speedX = speed * deltaX / deltaY;
			speedY = speed;
		}

		final boolean xSpeedIsPositive, ySpeedIsPositive;
		if (getMode() == Mode.AutoScrollingForward) {
			xSpeedIsPositive = cornerX == 0;
			ySpeedIsPositive = cornerY == 0;
		} else {
			xSpeedIsPositive = cornerX != 0;
			ySpeedIsPositive = cornerY != 0;
		}

		if (xSpeedIsPositive) {
			myEndX += speedX;
			if (myEndX >= boundX) {
				terminate();
			}
		} else {
			myEndX -= speedX;
			if (myEndX <= boundX) {
				terminate();
			}
		}

		if (ySpeedIsPositive) {
			myEndY += speedY;
			if (myEndY >= boundY) {
				terminate();
			}
		} else {
			myEndY -= speedY;
			if (myEndY <= boundY) {
				terminate();
			}
		}
	}
}
