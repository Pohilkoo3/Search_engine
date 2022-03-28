import javax.persistence.*;
@Entity
@Table(name = "index_search")
public class IndexSearch
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "page_id", nullable = false)
    private int page_id;

    @Column(name = "lemma_id", nullable = false)
    private int lemma_id;

    @Column(name = "rankIndex", nullable = false)
    private float rankIndex;

    public IndexSearch() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public int getLemma_id() {
        return lemma_id;
    }

    public void setLemma_id(int lemma_id) {
        this.lemma_id = lemma_id;
    }

    public float getRankIndex() {
        return rankIndex;
    }

    public void setRankIndex(float rankIndex) {
        this.rankIndex = rankIndex;
    }
}
