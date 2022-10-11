package pl.szkolaspringa.bookstore.order.domain;

public enum OrderStatus {
    NEW {
        @Override
        public boolean isUpdatableTo(OrderStatus targetStatus) {
            return switch (targetStatus) {
                case PAID, CANCELLED, ABANDONED -> true;
                default -> false;
            };
        }
    },
    PAID {
        @Override
        public boolean isUpdatableTo(OrderStatus targetStatus) {
            return SHIPPED == targetStatus;
        }
    },
    CANCELLED {
        @Override
        public boolean shouldRevokeBooks() {
            return true;
        }
    },
    ABANDONED {
        @Override
        public boolean shouldRevokeBooks() {
            return true;
        }
    },
    SHIPPED;

    public boolean isUpdatableTo(OrderStatus targetStatus) {
        return false;
    }

    public void assertTransitionValid(OrderStatus targetStatus) {
        if (!isUpdatableTo(targetStatus))
            throw new IllegalArgumentException(
                    "Transition from " + this + " to " + targetStatus + " is forbidden");
    }

    public boolean shouldRevokeBooks() {
        return false;
    }
}
