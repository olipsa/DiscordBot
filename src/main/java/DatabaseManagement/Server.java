package DatabaseManagement;

public class Server {
    private String id;
    private String name;
    public Server(String id, String name){
        this.id=id;
        this.name=name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
