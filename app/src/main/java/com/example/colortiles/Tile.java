package com.example.colortiles;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Tile {
    int x, wid, h, hei, p;

    public Tile(int x, int h, int wid, int hei, int p) {
        this.x = x;
        this.wid = wid;
        this.h = h;
        this.hei = hei;
        this.p = p;
    }


    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.p);
        canvas.drawRect(x,h,wid,hei,paint);
    }

}
