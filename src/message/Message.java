package message;

public class Message {

    private String nameTo;

    private String nameFrom;

    private String content;

    public Message(String to,String from,String content) {
        nameFrom = from;
        nameTo = to;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getNameFrom() {
        return nameFrom;
    }

    public String getNameTo() {
        return nameTo;
    }
}
