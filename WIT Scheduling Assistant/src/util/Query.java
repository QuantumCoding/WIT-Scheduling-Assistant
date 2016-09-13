package util;

public class Query {
	public String key;
	public String value;
	
	public Query(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String toString() {
		return key + "=" + value;
	}
}