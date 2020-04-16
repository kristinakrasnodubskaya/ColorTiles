package com.example.colortiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class TilesView extends View {
    int k = 2; // 4x4 3x3 2x2 размерность поля
    int[][] colors = new int[k][k];
    Tile[][] tiles = new Tile[k][k];
    int[] rgb;
    boolean init_field = false;
    Bitmap image;


    public void setK(int k) {
        this.k = k;
        this.tiles = new Tile[k][k];
        setColors(k);
    }

    public void setColors(int k) {
        colors = new int[k][k];
        // заполнить массив colors случайными цветами
        rgb = new int[]{Color.RED, Color.GREEN, Color.BLUE};
        int cl;
        boolean same = true;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                cl = (int) (Math.random() * 3);
                colors[i][j] = rgb[cl];
                if (same) {
                    int p1 = colors[0][0];
                    same = (p1 == colors[i][j]);
                }
            }
        }
        // исключить закрашивание одним цветом
        if (same) {
            for (int a : rgb) {
                if (a != colors[0][0]) {
                    colors[0][0] = a;
                    break;
                }
            }
        }
    }

    public void init(int w, int h) {
        int l = (int) (Math.min(w, h) * 0.9 / 4); //length of tile
        int offset = (int) (Math.min(w, h) * 0.1 / 5); //offset_between
        int offset_center1 = (int) ((Math.max(w, h) - l * k - offset * (k + 1)) / 2); //offset_for_center
        int offset_center2 = (int) ((Math.min(w, h) - l * k - offset * (k + 1)) / 2); //offset_for_center

        int f1 = 0, f2 = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                int i1 = i + 1;
                int j1 = j + 1;
                if (h >= w) {
                    f1 = offset_center1;
                    f2 = offset_center2;
                } else {
                    f2 = offset_center1;
                    f1 = offset_center2;
                }

                int p = colors[i][j];
                tiles[i][j] = new Tile(l * j + offset * j1 + f2, l * i + offset * i1 + f1,
                        (l + offset) * j1 + f2, (l + offset) * i1 + f1, p);
            }
        }
    }

    public boolean check() {
        // проверить, не выиграли ли вы (все плитки одного цвета)
        int r = 0, g = 0, b = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (tiles[i][j].p == rgb[0]) {
                    r++;
                } else if (tiles[i][j].p == rgb[1]) {
                    g++;
                } else if (tiles[i][j].p == rgb[2]) {
                    b++;
                }
            }
        }
        return (r == k * k || g == k * k || b == k * k);
    }

    public int[] getCoords(int x, int y) {
        // найти карту, которой коснулись
        int[] coord = {-1, -1};
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                if (x >= tiles[i][j].x && x <= tiles[i][j].wid) {
                    if (y >= tiles[i][j].h && y <= tiles[i][j].hei) {
                        coord[0] = i;
                        coord[1] = j;
                        break;
                    }
                }
            }
        }
        return coord;
    }

    public ArrayList<Tile> getNeighbours(int[] coordinates) {
        int i = coordinates[0];
        int j = coordinates[1];
        ArrayList<Tile> neighb = new ArrayList<>();
        if (i != -1 && j != -1) {
            neighb.add(tiles[i][j]);
            if (i > 0) {
                neighb.add(tiles[i - 1][j]);
            }
            if (i < k - 1) {
                neighb.add(tiles[i + 1][j]);
            }
            if (j > 0) {
                neighb.add(tiles[i][j - 1]);
            }
            if (j < k - 1) {
                neighb.add(tiles[i][j + 1]);
            }
        }
        return neighb;
    }

    public void repaint(int x, int y) {
        // смена цветов r -> g -> b
        ArrayList<Tile> neighbours = getNeighbours(getCoords(x, y));
        if (neighbours.size() > 0) {
            for (Tile t : neighbours) {
                if (t.p == rgb[rgb.length - 1]) {
                    t.p = rgb[0];
                    int[] cor = getCoords(t.x, t.h);    // изменение цвета в colors
                    colors[cor[0]][cor[1]] = rgb[0];   // для запоминания при повороте
                } else {
                    for (int i = 0; i < rgb.length - 1; i++) {
                        if (t.p == rgb[i]) {
                            t.p = rgb[i + 1];
                            int[] cor = getCoords(t.x, t.h);
                            colors[cor[0]][cor[1]] = rgb[i + 1];
                            break;
                        }
                    }
                }
            }
        }
    }

    public TilesView(Context context) {
        super(context);
    }

    public TilesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ////
    }


    // ========== preserving scroll position during screen rotations
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d("trans", "view in Save");
        SavedState st = new SavedState(super.onSaveInstanceState());
        st.dim = k;
        st.col = colors;
        return st;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d("trans", "view in Rest");
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.colors = ss.col;
        this.k = ss.dim;
    }


    public static class SavedState extends BaseSavedState {
        int dim;
        int[][] col;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(dim);
            out.writeArray(col);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcel in) {
            super(in);
            dim = in.readInt();
            col = (int[][]) in.readArray(getClass().getClassLoader());
        }
    }
// ==========


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(47, 79, 79));

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        // инициализировать плитки при создании
        if (!init_field) {
            init(width, height);
            init_field = true;
        }
        // отрисовка плиток
        for (Tile[] til : tiles) {
            for (Tile t : til) {
                t.draw(canvas);
            }
        }
        if (image != null) {
            canvas.drawBitmap(image, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!check()) {
            // координаты касания
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // палец коснулся экрана
                repaint(x, y);
            }
        } else {
            Toast.makeText(getContext(), "Вы выиграли!!!", Toast.LENGTH_LONG).show();
            image = BitmapFactory.decodeResource(getResources(), R.drawable.prize);
        }
        invalidate(); // заставляет экран перерисоваться
        return true;
    }

}
