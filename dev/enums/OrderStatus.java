package enums;

public enum OrderStatus {
    IN_PROCESS {
        @Override
        public String toString() {
            return "In Process";
        }
    },
    READY {
        @Override
        public String toString() {
            return "Ready";
        }
    },
    DONE {
        @Override
        public String toString() {
            return "Done";
        }
    };
}
