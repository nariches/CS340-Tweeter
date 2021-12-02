package edu.byu.cs.tweeter.model.domain;

public class UserDTO {

    private String alias;
    private String name;
    public UserDTO() {};

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
