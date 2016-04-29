package sos.rest.api

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject

class ApiController {

    def usersList(){
        JSONObject data = request.JSON
        log.info("Request: userList ----- " + data.toString())

        //String username = data?.username
        def mapRes  = [:]
        try {
            def userList = User.findAll()
            def resList = []
            userList.each {
                def map = [:]
                map.put("username",it.username)
                map.put("name",it.name)
                map.put("dateCreated",it.dateCreated)

                resList.add(map)
            }
            mapRes.put("result", "Ok")
            mapRes.put("userList", resList)
        } catch (Exception e) {
            mapRes.put("result","Error")
        }
        log.info("Response: userList ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def addUser(){
        JSONObject data = request.JSON
        log.info("Request: addUser ----- " + data.toString())
        String username = data?.username
        String name = data?.name
        String password = data?.password

        def mapRes  = [:]

        if(username && name && password){
            User user = new User()
            user.username = username
            user.name = name
            user.password = password
            user.dateCreated = new Date()
            if(user.save()){
                mapRes.put("result","Ok")
            }
            else{
                log.error("Error while saving user at DB")
                mapRes.put("result","Error: User not saved at DB")
            }
        }
        else{
            mapRes.put("result", "Error: Incorrect parameters")
        }
        log.info("Response: userList ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def publishPost(){
        JSONObject data = request.JSON
        log.info("Request: publishPost ----- " + data.toString())
        String username = data?.username
        String password = data?.password
        String post = data?.post

        def mapRes   = [:]

        if(username && password && post){
            User usuario = User.findByUsernameAndPassword(username,password)
            if(usuario){
                Post post1 = new Post()
                post1.post = post
                post1.dateCreated = new Date()
                post1.user = usuario
                if(post1.save()){
                    mapRes.put("result","Ok")
                }
                else{
                    log.error("publishPost Error while saving Post at DB")
                    mapRes.put("result","Error: User not saved at DB")
                }
            }
            else{
                mapRes.put("result","Error: Username or password incorrect")
            }
        }
        else{
            mapRes.put("result", "Error: Incorrect parameters")
        }
        log.info("Response: publishPost ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def editPost(){

        JSONObject data = request.JSON
        log.info("Request: editPost ----- " + data.toString())
        def mapRes  = [:]

        def postID = data?.postID
        def username = data?.username
        def password = data?.password
        def post = data?.post

        if(postID && username && password && post){
            User user = User.findByUsernameAndPassword(username,password)
            if(user){
                Post post1 = Post.findByIdAndUser(postID,user)
                if(post1){
                    post1.post = post
                    if(post1.save()){
                        mapRes.put("result","Ok")
                    }
                    else{
                        log.error("Error while saving Post at DB")
                        mapRes.put("result","Error: Post not saved at DB")
                    }
                }
                else{
                    mapRes.put("result","Error: Post not found")
                }
            }
            else{
                mapRes.put("result","Error: Username or password incorrect")
            }

        }
        else{
            mapRes.put("result", "Error: Incorrect parameters")
        }
        log.info("Response: editPost ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def deletePost(){
        JSONObject data = request.JSON
        log.info("Request: deletePost ----- " + data.toString())

        def postID = data?.postID
        def username = data?.username
        def password = data?.password
        def mapRes = [:]

        if(postID && username && password){
            User user = User.findByUsernameAndPassword(username,password)
            if(user){
                Post post1 = Post.findByIdAndUser(postID,user)
                if(post1){
                    try{
                        post1.delete(failOnError:true)
                        mapRes.put("result","Ok")
                    }
                    catch(e) {
                        mapRes.put("result","Error: Post not deleted")
                    }
                }
                else{
                    mapRes.put("result","Error: Post not found")
                }
            }
            else{
                mapRes.put("result","Error: Username or password incorrect")
            }

        }
        else{
            mapRes.put("result", "Error: Incorrect parameters")
        }
        log.info("Response: deletePost ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def getPosts(){
        JSONObject data = request.JSON
        log.info("Request: getPosts ----- " + data.toString())
        def mapRes = [:]

        def username = data?.username
        def password = data?.password
        def username2 = data?.username2
        def startDate = data?.startDate
        def endDate = data?.endDate
        def quantity = data?.quantity

        if(username && password && username2){
            User user1 = User.findByUsernameAndPassword(username,password)
            User user2 = User.findByUsername(username2)
            if(user1){
                def postList
                if(username.equals(username2)){
                    if(startDate && endDate && quantity){
                        postList  = Post.findAllByUserAndDateCreatedBetween(user1,startDate,endDate,[sort:"dateCreated",order:"desc",max: quantity])
                    }
                    else if(quantity){
                        postList  = Post.findAllByUser(user1,[sort:"dateCreated",order:"desc",max: quantity])
                    }
                    else if(startDate && endDate){
                        postList  = Post.findAllByUserAndDateCreatedBetween(user1,startDate,endDate,[sort:"dateCreated",order:"desc"])
                    }
                    else{
                        postList  = Post.findAllByUser(user1,[sort:"dateCreated",order:"desc"])
                    }

                    def resList = []
                    postList.each {
                        def map = [:]
                        map.put("username",it.user.username)
                        map.put("post",it.post)
                        map.put("dateCreated",it.dateCreated)

                        resList.add(map)
                    }
                    mapRes.put("result", "Ok")
                    mapRes.put("userList", resList)

                }
                else{
                    Friends friends = Friends.findByNameAndFriendOf(user2,user1)
                    if(friends){
                        if(startDate && endDate && quantity){
                            postList  = Post.findAllByUserAndDateCreatedBetween(user2,startDate,endDate,[sort:"dateCreated",order:"desc",max: quantity])
                        }
                        else if(quantity){
                            postList  = Post.findAllByUser(user2,[sort:"dateCreated",order:"desc",max: quantity])
                        }
                        else if(startDate && endDate){
                            postList  = Post.findAllByUserAndDateCreatedBetween(user2,startDate,endDate,[sort:"dateCreated",order:"desc"])
                        }
                        else{
                            postList  = Post.findAllByUser(user2,[sort:"dateCreated",order:"desc"])
                        }

                        def resList = []
                        postList.each {
                            def map = [:]
                            map.put("username",it.user.username)
                            map.put("post",it.post)
                            map.put("dateCreated",it.dateCreated)

                            resList.add(map)
                        }
                        mapRes.put("result", "Ok")
                        mapRes.put("userList", resList)
                    }
                }
            }
            else{
                mapRes.put("result","Error: Username or password incorrect")
            }
        }
        else{
            mapRes.put("result", "Error: Incorrect parameters")
        }
        log.info("Response: getPosts ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def getPostNumber(){
        JSONObject data = request.JSON
        log.info("Request: getPostNumber ----- " + data.toString())
        def username = data?.username
        def password = data?.password
        def mapRes = [:]
        if(username && password){
            User user1 = User.findByUsernameAndPassword(username,password)
            if(user1){
                def postList = Post.findAllByUser(user1)
                mapRes.put("result", "Ok")
                mapRes.put("quantity", postList.size())
            }
        }


        log.info("Response: getPostNumber ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def searchFriend(){
        JSONObject data = request.JSON
        log.info("Request: searchFriend ----- " + data.toString())
        def username = data?.username

        def mapRes = [:]

        if(username){
            User user1 = User.findByUsername(username)
            if(user1){
                mapRes.put("result","Ok")
                mapRes.put("username",user1.username)
                mapRes.put("name",user1.name)
            }
            else{
                List<User> userList = User.findAllByUsernameLike(username +"%")
                if(!userList.empty){
                    def resList = []
                    userList.each {
                        def map = [:]
                        map.put("username",it.username)
                        map.put("name",it.name)

                        resList.add(map)
                        mapRes.put("result", "Ok")
                        mapRes.put("userList", resList)
                    }
                }
                else{
                    mapRes.put("result","User not found")
                }
            }
        }
        else{
            mapRes.put("result", "Error: Incorrect parameters")
        }
        log.info("Response: searchFriend ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def addFriend(){
        JSONObject data = request.JSON
        log.info("Request: addFriend ----- " + data.toString())
        def username = data?.username
        def password = data?.password
        def username2 = data?.username2

        def mapRes = [:]
        if(username && password && username2){
            User user1 = User.findByUsernameAndPassword(username,password)
            if(user1){
                User user2 = User.findByUsername(username2)
                def friends1 = Friends.findByNameAndFriendOf(user1,user2)
                if(user2 && !friends1){
                    Friends friends = new Friends()
                    friends.name = user1
                    friends.friendOf = user2
                    Friends friends2 = new Friends()
                    friends2.name = user2
                    friends2.friendOf = user1
                    if(friends.save() && friends2.save()){
                        mapRes.put("result","Ok")
                    }
                    else{
                        mapRes.put("result","Error: Friends not saved at DB")
                    }
                }
                else{
                    mapRes.put("result","username2 not found or already friends")
                }
            }
            else{
                mapRes.put("result","Error: Username or password incorrect")
            }
        }
        else{
            mapRes.put("result", "Error: Incorrect parameters")
        }
        log.info("Response: addFriend ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def deleteFriend(){
        JSONObject data = request.JSON
        log.info("Request: deleteFriend ----- " + data.toString())
        def username = data?.username
        def password = data?.password
        def username2 = data?.username2

        def mapRes = [:]
        if(username && password && username2){
            User user1 = User.findByUsernameAndPassword(username,password)
            if(user1){
                User user2 = User.findByUsername(username2)
                def friends1 = Friends.findByNameAndFriendOf(user1,user2)
                def friends2 = Friends.findByNameAndFriendOf(user2,user1)
                if(user2 && friends1 && friends2){

                    try{
                        friends1.delete(failOnError:true)
                        friends2.delete(failOnError:true)
                        mapRes.put("result","Ok")
                    }
                    catch(e) {
                        mapRes.put("result","Error: Friends not deleted at DB")
                    }

                }
                else{
                    mapRes.put("result","username2 not found")
                }
            }
            else{
                mapRes.put("result","Error: Username or password incorrect")
            }
        }
        else{
            mapRes.put("result", "Error: Incorrect parameters")
        }
        log.info("Response: deleteFriend ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def getFriends(){
        JSONObject data = request.JSON
        log.info("Request: getFriends ----- " + data.toString())
        def username = data?.username
        def mapRes = [:]
        if(username){
            User user1 = User.findByUsername(username)
            if(user1){
                List<Friends> friendList = Friends.findAllByFriendOf(user1)
                def resList = []
                friendList.each {
                    def map = [:]
                    map.put("username",it.name.username)
                    map.put("name",it.name.name)

                    resList.add(map)
                }
                mapRes.put("result", "Ok")
                mapRes.put("userList", resList)
            }
            else{
                mapRes.put("result","username not found")
            }
        }
        else{
            mapRes.put("result", "Error: Incorrect parameters")
        }
        log.info("Response: getFriends ----- " + mapRes.toString())
        render mapRes as JSON
    }

    def deleteUser(){
        JSONObject data = request.JSON
        log.info("Request: deleteUser ----- " + data.toString())
        def mapRes = [:]

        def username = data?.username
        def password = data?.password

        if(username && password){
            User user = User.findByUsernameAndPassword(username,password)
            if(user){
                try{
                    user.delete(failOnError:true)
                    mapRes.put("result","Ok")
                }
                catch(e) {
                    mapRes.put("result","Error: User not deleted")
                }


            }
            else{
                mapRes.put("result","Error: Username or password incorrect")
            }
        }
        else{
            mapRes.put("result", "Error: Incorrect parameters")
        }

        log.info("Response: deleteUser ----- " + mapRes.toString())
        render mapRes as JSON
    }
}
