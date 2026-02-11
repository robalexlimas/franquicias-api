package co.com.bancolombia.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "test_connection")
public class TestDocument {
    @Id
    private String id;

    private String value;

    public TestDocument() {}

    public TestDocument(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() { return id; }
    public String getValue() { return value; }
    public void setId(String id) { this.id = id; }
    public void setValue(String value) { this.value = value; }
}