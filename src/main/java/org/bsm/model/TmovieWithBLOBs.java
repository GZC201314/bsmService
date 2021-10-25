package org.bsm.model;

import java.io.Serializable;

/**
 * tmovie
 * @author 
 */
public class TmovieWithBLOBs extends Tmovie implements Serializable {
    private String introduce;

    private String describetion;

    private static final long serialVersionUID = 1L;

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getDescribetion() {
        return describetion;
    }

    public void setDescribetion(String describetion) {
        this.describetion = describetion;
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
        TmovieWithBLOBs other = (TmovieWithBLOBs) that;
        return (this.getSerialNumber() == null ? other.getSerialNumber() == null : this.getSerialNumber().equals(other.getSerialNumber()))
            && (this.getPictureAddress() == null ? other.getPictureAddress() == null : this.getPictureAddress().equals(other.getPictureAddress()))
            && (this.getMovieName() == null ? other.getMovieName() == null : this.getMovieName().equals(other.getMovieName()))
            && (this.getStar() == null ? other.getStar() == null : this.getStar().equals(other.getStar()))
            && (this.getEvaluate() == null ? other.getEvaluate() == null : this.getEvaluate().equals(other.getEvaluate()))
            && (this.getIntroduce() == null ? other.getIntroduce() == null : this.getIntroduce().equals(other.getIntroduce()))
            && (this.getDescribetion() == null ? other.getDescribetion() == null : this.getDescribetion().equals(other.getDescribetion()));
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
        result = prime * result + ((getIntroduce() == null) ? 0 : getIntroduce().hashCode());
        result = prime * result + ((getDescribetion() == null) ? 0 : getDescribetion().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", introduce=").append(introduce);
        sb.append(", describetion=").append(describetion);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}