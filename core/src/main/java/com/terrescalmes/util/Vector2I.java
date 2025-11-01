package com.terrescalmes.util;

import com.badlogic.gdx.math.Vector2;

public class Vector2I {
    public int x;
    public int y;

    public Vector2I() {
        this(0, 0);
    }

    public Vector2I(float x, float y) {
        Vector2 tempo = new Vector2(x, y);
        Vector2I newTempo = Vector2I.from(tempo);
        this.x = newTempo.x;
        this.y = newTempo.y;
    }

    public Vector2I(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2I cpy() {
        return new Vector2I(x, y);
    }

    // conversion vers Vector2 (float) — la plus utile
    public Vector2 toVector2() {
        return new Vector2(x, y);
    }

    // constructeur depuis Vector2 (arrondi)
    public static Vector2I from(Vector2 v) {
        return new Vector2I(Math.round(v.x), Math.round(v.y));
    }

    // opérations basiques entières si tu en as besoin
    public Vector2I add(Vector2I other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2I sub(Vector2I other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vector2I set(Vector2I other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    // distance euclidienne (retourne int — arrondi)
    public int len() {
        return (int) Math.sqrt((long) x * x + (long) y * y);
    }

    // distance vers un Vector2 (float)
    public float dst(Vector2 v) {
        float dx = x - v.x;
        float dy = y - v.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
