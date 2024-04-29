import enums.EventType;

import java.util.Objects;

public class Event implements Comparable<Event>{

        private final double time;
        private final EventType type;
        private final int idInit;
        private final Integer idFinish;

        public Event(EventType type, double time, int idInit) {
            this.type = type;
            this.time = time;
            this.idInit = idInit;
            this.idFinish = null;
        }

        public Event(EventType type, double time, int idInit, int idFinish) {
            this.type = type;
            this.time = time;
            this.idInit = idInit;
            this.idFinish = idFinish;
        }

        // Getter métodos com comentários Javadoc
        public double getTime() {
            return time;
        }

        public EventType getType() {
            return type;
        }

        public int getIdInit() {
            return idInit;
        }

        public Integer getIdFinish() {
            return idFinish;
        }

        @Override
        public int compareTo(Event e) {
            return Double.compare(this.time, e.time);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Event event = (Event) o;
            return Double.compare(event.time, time) == 0 &&
                    idInit == event.idInit &&
                    Objects.equals(idFinish, event.idFinish) &&
                    type == event.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(time, type, idInit, idFinish);
        }
}
