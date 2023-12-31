package it.unimib.fipavonline.model;

/**
 * Class that represents the result of an action that requires
 * the use of a Web Service or a local database.
 */
public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        if (this instanceof CampionatoResponseSuccess ||
                this instanceof PartitaResponseSuccess ||
                this instanceof UserResponseSuccess) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class CampionatoResponseSuccess extends Result {
        private final CampionatoResponse campionatoResponse;
        public CampionatoResponseSuccess(CampionatoResponse campionatoResponse) {
            this.campionatoResponse = campionatoResponse;
        }
        public CampionatoResponse getData() {
            return campionatoResponse;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class PartitaResponseSuccess extends Result {
        private final PartitaResponse partitaResponse;
        public PartitaResponseSuccess(PartitaResponse partitaResponse) {
            this.partitaResponse = partitaResponse;
        }
        public PartitaResponse getData() {
            return partitaResponse;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class UserResponseSuccess extends Result {
        private final User user;
        public UserResponseSuccess(User user) {
            this.user = user;
        }
        public User getData() {
            return user;
        }
    }

    /**
     * Class that represents an error occurred during the interaction
     * with a Web Service or a local database.
     */
    public static final class Error extends Result {
        private final String message;
        public Error(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
}
