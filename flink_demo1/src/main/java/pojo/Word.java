package pojo;

public class Word {

    public String word;
    public int count;

    public Word() {
    }

    public Word(String word, int count) {
        this.word = word;
        this.count = count;
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", count=" + count +
                '}';
    }
}
