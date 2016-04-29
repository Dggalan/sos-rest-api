package sos.rest.api

class User {



    String username
    String name
    String password

    Date dateCreated



    static constraints = {

        username blank: false, unique: true
        password blank: false
        name nullable: true
        dateCreated nullable : false

    }










}
