package domainLayer;

import java.util.Objects;

public class Contact {
    private String name;
    private String phoneNumber;

    // Constructor
    public Contact(String name, String phoneNumber) {
        setName(name);
        setPhoneNumber(phoneNumber);
    }

    // Getters and Setters
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact name cannot be empty.");
        }
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        // Check if the input is null or empty
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        // Remove spaces and dashes
        String cleanedPhone = phoneNumber.replaceAll("[\\s-]", "");

        // Check if the cleaned phone contains only digits
        if (!cleanedPhone.matches("\\d+")) {
            throw new IllegalArgumentException("Phone number must contain only digits.");
        }
        // Format the phone number and save it
        this.phoneNumber = formatPhoneNumber(cleanedPhone);
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Contact contact = (Contact) o;
        return Objects.equals(phoneNumber, contact.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }

    @Override
    public String toString() {
        return "Contact (Name: " + name + ", PhoneNumber: " + phoneNumber + ")";
    }

    // Private helper to format phone numbers
    private String formatPhoneNumber(String phone) {
        // At this point, 'phone' contains only digits (0-9)
        if (phone.length() == 10) {
            return phone.substring(0, 3) + "-" + phone.substring(3);
        }
        if (phone.length() == 9) {
            return "0" + phone.substring(0,2) + "-" + phone.substring(2);
        }
        // If length is wrong, throw an exception
        throw new IllegalArgumentException("Invalid phone number length. Must be 9 or 10 digits.");
    }
}

