package ru.practicum.shareit.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String massage) {
        super(massage);
    }
}
