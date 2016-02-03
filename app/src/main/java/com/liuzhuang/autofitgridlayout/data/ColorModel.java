package com.liuzhuang.autofitgridlayout.data;

public class ColorModel {

    public int color;
    public int id;

    public ColorModel(final int id, final int color) {
        this.id = id;
        this.color = color;
    }

    @Override
    public boolean equals(final Object o) {
        return id == ((ColorModel) o).id;
    }
}