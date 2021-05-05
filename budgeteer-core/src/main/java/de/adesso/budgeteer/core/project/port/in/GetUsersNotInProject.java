package de.adesso.budgeteer.core.project.port.in;

import java.util.List;

public interface GetUsersNotInProject {
    List<String> getUsersNotInProject(long projectId);
}
