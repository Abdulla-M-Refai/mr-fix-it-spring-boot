package com.Mr.fix.it.Entity.Enum;

import lombok.Getter;

@Getter
public enum MessageType
{
    TEXT("TEXT"),
    IMAGE("IMAGE");

    private final String type;

    MessageType(String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return type;
    }
}
