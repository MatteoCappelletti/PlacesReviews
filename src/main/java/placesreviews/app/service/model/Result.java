package placesreviews.app.service.model;

public record Result(boolean ok, String errorMessage) {
    public static Result success() {
        return new Result(true, null);
    }

    public static Result error(String message) {
        return new Result(false, message);
    }
}
