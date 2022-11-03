package fr.gerard.discord.exceptions;

public class CloudflareException extends RuntimeException {

    public CloudflareException() {
        super();
    }

    public CloudflareException(String message) {
        super(message);
    }
}
