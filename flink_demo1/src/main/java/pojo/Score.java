package pojo;

import java.io.Serializable;

public class Score implements Serializable {

    public String name;
    public String course;
    public int score;

    public Score() {
    }

    public Score(String name, String course, int score) {
        this.name = name;
        this.course = course;
        this.score = score;
    }

    public static Score of(String name,String course,int score){
        return new Score(name,course,score);
    }

    @Override
    public String toString() {
        return "Score{" +
                "name='" + name + '\'' +
                ", course='" + course + '\'' +
                ", score=" + score +
                '}';
    }
}
