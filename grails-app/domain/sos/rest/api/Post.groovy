package sos.rest.api

class Post {

    User user
    Date dateCreated
    String post

    static constraints = {
        user  nullable:false
        dateCreated nullable : false
        post nullable: false

    }
}
