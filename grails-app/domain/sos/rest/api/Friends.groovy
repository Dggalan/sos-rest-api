package sos.rest.api

class Friends {

    User friendOf
    User name

    static constraints = {
        friendOf nullable: false
        name nullable : false
    }
}
