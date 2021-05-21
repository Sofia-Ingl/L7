package shared.data;

import java.io.Serializable;

/**
 * Координаты.
 */
public class Coordinates implements Serializable {

    private static final long serialVersionUID = 1L;

    private float x; //Максимальное значение поля: 326
    private Integer y; //Максимальное значение поля: 281, Поле не может быть null

    public Coordinates(float x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

