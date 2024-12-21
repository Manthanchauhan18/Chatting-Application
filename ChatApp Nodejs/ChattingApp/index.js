const express = require('express')
const http = require('http')
const socketIo = require('socket.io')
const userRouter = require('./routes/user')
const chatRouter = require('./routes/chat')
const conntectMongoDb = require('./connection')

const app = express()
const PORT = 9000
const httpServer = http.createServer(app)
const io = socketIo(httpServer)

const users = {};

//socket io code
io.on('connection', (socket) => {
    console.log("user connected",socket.id)

    socket.on("login", (data) => {
        // console.log(data.username, data.userId)
        let username = data.username
        let userId = data.userId

        users[userId] = {socketId: socket.id, username: username}

        // users.push({username, userId , socketId: socket.id})
        // console.log(`${username} logged in`)
        console.log(users)
    })

    socket.on("send_message", (data) => {
        console.log(data)
        // console.log(users)
        // const reciptent = users.find((user) => user.userId === data.to)
        // console.log("reciptent",reciptent)
        
        const reciptent = users[data.to]
        console.log(reciptent)

        if(reciptent){
            // console.log(reciptent.userId)
            io.to(reciptent.socketId).emit('received_message', {
                from: data.from,
                message: data.message
            })
        }else {
            console.log('Recipient not found.');
            // Optionally send an error message to the sender
          }
    })

    socket.on("disconnect", () => {
        // users.splice(user => user.socketId !== socket.id)
        // users.filter(user => user.socketId !== socket.id)
        const userId = Object.keys(users).find((key) => users[key].socketId === socket.id);
        console.log(userId)
        if (userId) {
            // If a matching user is found, delete that user from the 'users' object
            delete users[userId];
            console.log(`User ${userId} disconnected and removed from users.`);
        }
        // delete users[socket.id]; 
        console.log(users)
    })

})

// mongodb connection
conntectMongoDb("mongodb://127.0.0.1:27017/chatApp-Android")
.then(() => {console.log("Mongodb connected!")})
.catch((err) => {console.log(`Error: ${err}`)})

// middleware
app.use(express.json())
app.use(express.urlencoded({extended: false}))
// app.use(express.static(path.resolve('./public')))

// routes
app.use('/user', userRouter)
app.use('/chat', chatRouter)


httpServer.listen(PORT , () => {console.log(`Server started on port: localhost:${PORT}`)})

