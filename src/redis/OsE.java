package redis;

public enum OsE {

    Linux("Linux"),
    WINDOWS("Windos"),;

    OsE(String desc) {
        this.description = desc;
    }

    public String toString() {
        return description;
    }

    private String description;

}
