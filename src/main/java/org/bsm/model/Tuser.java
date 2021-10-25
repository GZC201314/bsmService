package org.bsm.model;

import java.io.Serializable;
import java.util.Date;

/**
 * tuser
 * @author 
 */
public class Tuser implements Serializable {
    private String name;

    private String id;

    private String pwd;

    private Date createdatetime;

    private Date lastmodifytime;

    private Integer roleid;

    private String userlog;

    private Integer isfacevalid;

    private static final long serialVersionUID = 1L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Date getCreatedatetime() {
        return createdatetime;
    }

    public void setCreatedatetime(Date createdatetime) {
        this.createdatetime = createdatetime;
    }

    public Date getLastmodifytime() {
        return lastmodifytime;
    }

    public void setLastmodifytime(Date lastmodifytime) {
        this.lastmodifytime = lastmodifytime;
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public String getUserlog() {
        return userlog;
    }

    public void setUserlog(String userlog) {
        this.userlog = userlog;
    }

    public Integer getIsfacevalid() {
        return isfacevalid;
    }

    public void setIsfacevalid(Integer isfacevalid) {
        this.isfacevalid = isfacevalid;
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
        Tuser other = (Tuser) that;
        return (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPwd() == null ? other.getPwd() == null : this.getPwd().equals(other.getPwd()))
            && (this.getCreatedatetime() == null ? other.getCreatedatetime() == null : this.getCreatedatetime().equals(other.getCreatedatetime()))
            && (this.getLastmodifytime() == null ? other.getLastmodifytime() == null : this.getLastmodifytime().equals(other.getLastmodifytime()))
            && (this.getRoleid() == null ? other.getRoleid() == null : this.getRoleid().equals(other.getRoleid()))
            && (this.getUserlog() == null ? other.getUserlog() == null : this.getUserlog().equals(other.getUserlog()))
            && (this.getIsfacevalid() == null ? other.getIsfacevalid() == null : this.getIsfacevalid().equals(other.getIsfacevalid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPwd() == null) ? 0 : getPwd().hashCode());
        result = prime * result + ((getCreatedatetime() == null) ? 0 : getCreatedatetime().hashCode());
        result = prime * result + ((getLastmodifytime() == null) ? 0 : getLastmodifytime().hashCode());
        result = prime * result + ((getRoleid() == null) ? 0 : getRoleid().hashCode());
        result = prime * result + ((getUserlog() == null) ? 0 : getUserlog().hashCode());
        result = prime * result + ((getIsfacevalid() == null) ? 0 : getIsfacevalid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", name=").append(name);
        sb.append(", id=").append(id);
        sb.append(", pwd=").append(pwd);
        sb.append(", createdatetime=").append(createdatetime);
        sb.append(", lastmodifytime=").append(lastmodifytime);
        sb.append(", roleid=").append(roleid);
        sb.append(", userlog=").append(userlog);
        sb.append(", isfacevalid=").append(isfacevalid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}