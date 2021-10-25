package org.bsm.model;

import java.io.Serializable;

/**
 * person
 * @author GZC
 */
public class Person implements Serializable {
    private Integer personid;

    private String firstname;

    private String lastname;

    private static final long serialVersionUID = 1L;

    public Integer getPersonid() {
        return personid;
    }

    public void setPersonid(Integer personid) {
        this.personid = personid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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
        Person other = (Person) that;
        return (this.getPersonid() == null ? other.getPersonid() == null : this.getPersonid().equals(other.getPersonid()))
            && (this.getFirstname() == null ? other.getFirstname() == null : this.getFirstname().equals(other.getFirstname()))
            && (this.getLastname() == null ? other.getLastname() == null : this.getLastname().equals(other.getLastname()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPersonid() == null) ? 0 : getPersonid().hashCode());
        result = prime * result + ((getFirstname() == null) ? 0 : getFirstname().hashCode());
        result = prime * result + ((getLastname() == null) ? 0 : getLastname().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", personid=").append(personid);
        sb.append(", firstname=").append(firstname);
        sb.append(", lastname=").append(lastname);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}