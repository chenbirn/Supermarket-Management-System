package enums;

public enum UnitType {
    KG {
        @Override
        public String toString() {
            return "kilograms";
        }
    },
    LITERS {
        @Override
        public String toString() {
            return "liters";
        }
    },
    UNITS {
        @Override
        public String toString() {
            return "units";
        }
    };
}
