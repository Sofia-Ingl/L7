package shared.data;

import java.io.Serializable;
import java.util.Objects;

/**
 * Человек.
 */
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name; //Поле не может быть null, Строка не может быть пустой
    private Long height; //Поле не может быть null, Значение поля должно быть больше 0
    private Color eyeColor; //Поле может быть null
    private Country nationality; //Поле может быть null

    public Person(String name, Long height, Color eyeColor, Country nationality) {
        this.name = name;
        this.height = height;
        this.eyeColor = eyeColor;
        this.nationality = nationality;
    }

    public String getName() {
        return name;
    }

    public Long getHeight() {
        return height;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Country getNationality() {
        return nationality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(getName(), person.getName()) &&
                Objects.equals(getHeight(), person.getHeight()) &&
                getEyeColor() == person.getEyeColor() &&
                getNationality() == person.getNationality();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getHeight(), getEyeColor(), getNationality());
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", eyeColor=" + eyeColor +
                ", nationality=" + nationality +
                '}';
    }
}
