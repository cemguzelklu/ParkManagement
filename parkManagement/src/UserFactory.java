public class UserFactory {
    public static User createUser(String role, String username) {
        if ("admin".equals(role)) {
            return new Admin(username); // Admin rolü için
        } else if ("user".equals(role)) {
            return new Customer(username); // User rolü için
        }
        return null; // Belirlenemeyen rol
    }
}
