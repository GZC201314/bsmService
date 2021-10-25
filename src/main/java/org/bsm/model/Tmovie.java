package org.bsm.model;

import java.io.Serializable;

/**
 * tmovie
 * @author 
 */
public class Tmovie implements Serializable {
    private String serialNumber;

    private String pictureAddress;

    private String movieName;

    private Float star;

    private String evaluate;

    private static final long serialVersionUID = 1L;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Float getStar() {
        return star;
    }

    public void setStar(Float star) {
        this.star = star;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
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
        Tmovie other = (Tmovie) that;
        return (this.getSerialNumber() == null ? other.getSerialNumber() == null : this.getSerialNumber().equals(other.getSerialNumber()))
            && (this.getPictureAddress() == null ? other.getPictureAddress() == null : this.getPictureAddress().equals(other.getPictureAddress()))
            && (this.getMovieName() == null ? other.getMovieName() == null : this.getMovieName().equals(other.getMovieName()))
            && (this.getStar() == null ? other.getStar() == null : this.getStar().equals(other.getStar()))
            && (this.getEvaluate() == null ? other.getEvaluate() == null : this.getEvaluate().equals(other.getEvaluate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSerialNumber() == null) ? 0 : getSerialNumber().hashCode());
        result = prime * result + ((getPictureAddress() == null) ? 0 : getPictureAddress().hashCode());
        result = prime * result + ((getMovieName() == null) ? 0 : getMovieName().hashCode());
        result = prime * result + ((getStar() == null) ? 0 : getStar().hashCode());
        result = prime * result + ((getEvaluate() == null) ? 0 : getEvaluate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", serialNumber=").append(serialNumber);
        sb.append(", pictureAddress=").append(pictureAddress);
        sb.append(", movieName=").append(movieName);
        sb.append(", star=").append(star);
        sb.append(", evaluate=").append(evaluate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}