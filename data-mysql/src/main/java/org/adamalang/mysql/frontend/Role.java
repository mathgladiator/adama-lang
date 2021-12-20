package org.adamalang.mysql.frontend;

public enum Role {
    None(0x00),
    Developer(0x01),
    Owner(0x02);

    public int role;

    private Role(int role) {
        this.role = role;
    }
}
