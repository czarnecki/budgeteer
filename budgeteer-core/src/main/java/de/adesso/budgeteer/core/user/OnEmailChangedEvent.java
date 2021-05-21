package de.adesso.budgeteer.core.user;

import org.springframework.context.ApplicationEvent;

public class OnEmailChangedEvent extends ApplicationEvent {
    private final long userId;
    private final String name;
    private final String email;

    public OnEmailChangedEvent(long userId, String name, String email) {
        super(userId);
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
