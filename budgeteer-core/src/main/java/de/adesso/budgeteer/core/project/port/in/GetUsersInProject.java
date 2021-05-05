package de.adesso.budgeteer.core.project.port.in;

import java.util.List;

public interface GetUsersInProject {
    List<String> getUsersInProject(long projectId);
}
