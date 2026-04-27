package enums;

public enum PackagingOption {
    SINGLE_UNITS {
        @Override
        public String toString() {
            return "single units";
        }
    },
    BOX_ONLY {
        @Override
        public String toString() {
            return "box only";
        }
    },
    BOTH {
        @Override
        public String toString() {
            return "both options";
        }
    };


}
