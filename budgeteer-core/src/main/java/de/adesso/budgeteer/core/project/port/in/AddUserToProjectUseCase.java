package de.adesso.budgeteer.core.project.port.in;

public interface AddUserToProjectUseCase {
    void addUserToProject(long projectId, long userId);
}
