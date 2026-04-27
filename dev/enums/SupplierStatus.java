package enums;

public enum SupplierStatus {
    ACTIVE {
        @Override
        public String toString() {
            return "Active";
        }
    },
    INACTIVE {
        @Override
        public String toString() {
            return "Inactive";
        }
    };
}
