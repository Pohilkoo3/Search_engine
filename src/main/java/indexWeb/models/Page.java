package indexWeb.models;
import javax.persistence.*;

@Entity
@Table(name = "page")
public class Page
{
    @Id
    private int id;

    @Column(length = 230, nullable = false)
    private String path;
    @Column(nullable = false)
    private int code;
    @Column(name = " content", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    public Page() {
    }

    public String getPath() {
        return path;
    }

    public int getId() {
        return id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "indexWeb.dao.Page{" +
                "id= " + id +
                ", path='" + path + '\'' +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

}
