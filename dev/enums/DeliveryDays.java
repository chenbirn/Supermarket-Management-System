package enums;

public enum DeliveryDays {
    SUNDAY {
        @Override
        public String toString() {
            return "Sunday";
        }
    },
    MONDAY {
        @Override
        public String toString() {
            return "Monday";
        }
    },
    TUESDAY {
        @Override
        public String toString() {
            return "Tuesday";
        }
    },
    WEDNESDAY {
        @Override
        public String toString() {
            return "Wednesday";
        }
    },
    THURSDAY {
        @Override
        public String toString() {
            return "Thursday";
        }
    },
    FRIDAY {
        @Override
        public String toString() {
            return "Friday";
        }
    },
    BY_ORDER {
        @Override
        public String toString() {
            return "By Order";
        }
    };
}
