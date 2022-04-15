package indexWeb.models;

import javax.persistence.*;

@Entity
@Table(name = "lemma")
public class Lemma
{
    @Id
    private int id;

    @Column(length = 200, nullable = false)
    private String lemma;

    @Column(updatable = true, nullable = false)
    private int frequency;


    public Lemma() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }


    @Override
    public String toString() {
        return "indexWeb.dao.Lemma{" +
                "id=" + id +
                ", lemma='" + lemma + '\'' +
                ", frequency=" + frequency +
                '}';
    }

}
