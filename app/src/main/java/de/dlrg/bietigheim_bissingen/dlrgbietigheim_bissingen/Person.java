package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

public class Person implements Comparable<Person> {
    public String name;
    public int abzeichen;
    public int sanitätsausbildung;
    public int rolle;

    public Person(String name, int abzeichen, int sanitätsausbildung, int rolle) {
        this.name = name;
        this.abzeichen = abzeichen;
        this.sanitätsausbildung = sanitätsausbildung;
        this.rolle = rolle;
    }

    @Override
    public int compareTo(Person o) {
        return this.name.compareTo(o.name);
    }
}
