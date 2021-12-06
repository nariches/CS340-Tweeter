package edu.byu.cs.tweeter.model.domain;

public class UserDTO {

    private String alias;
    private String name;
    public UserDTO() {};

    public UserDTO(String alias) {
        this.alias = alias;
    }

    public UserDTO(String alias, String name) {
        this.alias = alias;
        this.name = name;
    }


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
