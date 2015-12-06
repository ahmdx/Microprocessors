import java.util.Arrays;

public class CacheEntry {
	private boolean dirty;
	private int tag;
	private String[] data;
	private int size;

	public CacheEntry(int size) {
		this.dirty = false;
		this.tag = 0;
		this.size = size;
		this.data = new String[size];
	}

	public CacheEntry(String[] data) {
		this.dirty = false;
		this.tag = 0;
		this.size = data.length;
		setData(data);
	}

	public CacheEntry(int tag, String[] data) {
		this.dirty = false;
		this.tag = tag;
		this.size = data.length;
		setData(data);
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}

	public String getByte(int offset) {
		return this.data[Math.min(this.size, offset)];
	}

	public void setByte(int offset, String data) {
		this.data[Math.min(this.size, offset)] = data;
	}

	public String toString() {
		return this.dirty + " " + this.tag + " " + Arrays.toString(this.data);
	}
}
