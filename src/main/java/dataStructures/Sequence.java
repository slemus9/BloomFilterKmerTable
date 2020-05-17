package dataStructures;

public class Sequence {

    private String id;
    private String characters;
    private String score;

    public Sequence (String id, String characters, String score) {
        this.id = id;
        this.characters = characters;
        this.score = score;
    }

    public Sequence (String id, String characters) {
        this.id = id;
        this.characters = characters;
        this.score = "";
    }

    public String getId () {
        return id;
    }

    public String getCharacters () {
        return characters;
    }

    public String getScore () {
        return score;
    }
}
