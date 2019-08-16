package com.meynier.ldap.enums;

public enum LdapOperator {

    AND('&'),OR('|');

    private Character value;

    LdapOperator(Character value){
        this.value=value;
    }

    public Character getValue(){
        return value;
    }

}
