/** A subject/course offering that students can enroll in. */
public class Subject {
    private String code;
    private String title;
    private String course;
    private int units;
    private int capacity;

    public Subject(String code, String title, int units, int capacity) {
        this(code, title, "General", units, capacity);
    }

    public Subject(
            String code, String title, String course,
            int units, int capacity) {
        this.code = code;
        this.title = title;
        this.course = course;
        this.units = units;
        this.capacity = capacity;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String toCsv() {
        return code + "," + title + "," + units + "," + capacity
                + "," + course;
    }

    public static Subject fromCsv(String[] p) {
        String course = p.length > 4 ? p[4] : "General";
        return new Subject(
                p[0], p[1], course,
                Integer.parseInt(p[2]), Integer.parseInt(p[3])
        );
    }

    @Override
    public String toString() { return code + " - " + title; }
}
