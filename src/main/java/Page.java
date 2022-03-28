import javax.persistence.*;
import javax.swing.text.html.HTML;
@Entity
@Table(name = "page")
public class Page
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String path;
    @Column(nullable = false)
    private int code;
    @Column(name = "medium_text", length = 1111111, nullable = false)
    private String mediumText;

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

    public String getMediumText() {
        return mediumText;
    }

    public void setMediumText(String mediumText) {
        this.mediumText = mediumText;
    }
}
