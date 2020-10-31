package com.example.duodraw;

import android.graphics.Path;

public class FingerPath {

    public int color;
    public boolean emboss;
    public boolean blur;
    public int strokeWidth;
    public Path path;
//класс, который сохраняет цвет
    public FingerPath(int color, boolean emboss, boolean blur, int strokeWidth, Path path) {
        this.color = color;//цвет
        this.emboss = emboss;//тень
        this.blur = blur;//размытие
        this.strokeWidth = strokeWidth;//ширина линии
        this.path = path;//путь
    }
}