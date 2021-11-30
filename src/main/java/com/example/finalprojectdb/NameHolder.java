package com.example.finalprojectdb;

public final class NameHolder {

        private String name; //username
        private String role;
        private final static NameHolder INSTANCE = new NameHolder();

        private NameHolder() {}

        public static NameHolder getInstance() {
            return INSTANCE;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setRole(String role){
            this.role = role;
        }

        public String getRole(){
            return role;
        }

}

