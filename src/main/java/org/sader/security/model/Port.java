package org.sader.security.model;

import java.util.Objects;
import java.util.logging.Level;

public class Port {
    private int number;
    private PortStatus status;

    public Port(int number){
        setNumber(number);
        status = PortStatus.CLOSE;
    }

    public Port(int number, PortStatus status) {
        this.number = number;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Port port = (Port) o;
        return number == port.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public PortStatus getStatus() {
        return status;
    }

    public void setStatus(PortStatus status) {
        this.status = status;
    }

    public enum PortStatus {
        OPEN("OPEN"), CLOSE("CLOSE");
        private String text;

        private PortStatus(String text){
            this.text = text;
        }
    }
}
