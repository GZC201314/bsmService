package org.bsm.model;

import java.io.Serializable;

/**
 * doubanbook
 * @author 
 */
public class Doubanbook implements Serializable {
    private String isbn;

    private String name;

    private String englishname;

    private String title;

    private String seriesname;

    private String author;

    private String publisher;

    private String publishingtime;

    private String edition;

    private String score;

    private String translate;

    private String editor;

    private String folio;

    private String size;

    private String weight;

    private String price;

    private String image;

    private String introduction;

    private static final long serialVersionUID = 1L;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishname() {
        return englishname;
    }

    public void setEnglishname(String englishname) {
        this.englishname = englishname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSeriesname() {
        return seriesname;
    }

    public void setSeriesname(String seriesname) {
        this.seriesname = seriesname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishingtime() {
        return publishingtime;
    }

    public void setPublishingtime(String publishingtime) {
        this.publishingtime = publishingtime;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Doubanbook other = (Doubanbook) that;
        return (this.getIsbn() == null ? other.getIsbn() == null : this.getIsbn().equals(other.getIsbn()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getEnglishname() == null ? other.getEnglishname() == null : this.getEnglishname().equals(other.getEnglishname()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getSeriesname() == null ? other.getSeriesname() == null : this.getSeriesname().equals(other.getSeriesname()))
            && (this.getAuthor() == null ? other.getAuthor() == null : this.getAuthor().equals(other.getAuthor()))
            && (this.getPublisher() == null ? other.getPublisher() == null : this.getPublisher().equals(other.getPublisher()))
            && (this.getPublishingtime() == null ? other.getPublishingtime() == null : this.getPublishingtime().equals(other.getPublishingtime()))
            && (this.getEdition() == null ? other.getEdition() == null : this.getEdition().equals(other.getEdition()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getTranslate() == null ? other.getTranslate() == null : this.getTranslate().equals(other.getTranslate()))
            && (this.getEditor() == null ? other.getEditor() == null : this.getEditor().equals(other.getEditor()))
            && (this.getFolio() == null ? other.getFolio() == null : this.getFolio().equals(other.getFolio()))
            && (this.getSize() == null ? other.getSize() == null : this.getSize().equals(other.getSize()))
            && (this.getWeight() == null ? other.getWeight() == null : this.getWeight().equals(other.getWeight()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getImage() == null ? other.getImage() == null : this.getImage().equals(other.getImage()))
            && (this.getIntroduction() == null ? other.getIntroduction() == null : this.getIntroduction().equals(other.getIntroduction()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getIsbn() == null) ? 0 : getIsbn().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getEnglishname() == null) ? 0 : getEnglishname().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getSeriesname() == null) ? 0 : getSeriesname().hashCode());
        result = prime * result + ((getAuthor() == null) ? 0 : getAuthor().hashCode());
        result = prime * result + ((getPublisher() == null) ? 0 : getPublisher().hashCode());
        result = prime * result + ((getPublishingtime() == null) ? 0 : getPublishingtime().hashCode());
        result = prime * result + ((getEdition() == null) ? 0 : getEdition().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
        result = prime * result + ((getTranslate() == null) ? 0 : getTranslate().hashCode());
        result = prime * result + ((getEditor() == null) ? 0 : getEditor().hashCode());
        result = prime * result + ((getFolio() == null) ? 0 : getFolio().hashCode());
        result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
        result = prime * result + ((getWeight() == null) ? 0 : getWeight().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getImage() == null) ? 0 : getImage().hashCode());
        result = prime * result + ((getIntroduction() == null) ? 0 : getIntroduction().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", isbn=").append(isbn);
        sb.append(", name=").append(name);
        sb.append(", englishname=").append(englishname);
        sb.append(", title=").append(title);
        sb.append(", seriesname=").append(seriesname);
        sb.append(", author=").append(author);
        sb.append(", publisher=").append(publisher);
        sb.append(", publishingtime=").append(publishingtime);
        sb.append(", edition=").append(edition);
        sb.append(", score=").append(score);
        sb.append(", translate=").append(translate);
        sb.append(", editor=").append(editor);
        sb.append(", folio=").append(folio);
        sb.append(", size=").append(size);
        sb.append(", weight=").append(weight);
        sb.append(", price=").append(price);
        sb.append(", image=").append(image);
        sb.append(", introduction=").append(introduction);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}