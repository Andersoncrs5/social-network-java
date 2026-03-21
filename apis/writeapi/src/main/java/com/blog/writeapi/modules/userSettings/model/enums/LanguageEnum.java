package com.blog.writeapi.modules.userSettings.model.enums;

public enum LanguageEnum {
    PT_BR("pt-BR", "Português (Brasil)"),
    EN_US("en-US", "English (US)"),
    ES_ES("es-ES", "Español");

    private final String code;
    private final String label;

    LanguageEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
}