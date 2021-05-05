package de.adesso.budgeteer.core.common;

import lombok.Value;

import java.io.File;

@Value
public class Attachment {
    String fileName;
    String link;
    File file;
}
