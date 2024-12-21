const chatModel = require('../models/chat')
const userModel = require('../models/user')

async function handleGetChat(req, res){
    const {from , to} = req.query
    // console.log(from, to)

    const chats = await chatModel.find({
        $or: [
            { from: from, to: to },   // Messages from 'from' to 'to'
            { from: to, to: from }    // Messages from 'to' to 'from'
        ]
    }).sort({ createdAt: 1 });

    return res.status(200).json(chats)
}

async function handleCreateChat(req, res){
    const {from, to, message} = req.body
    try {
        await chatModel.create({
            from,
            to,
            message
        }).then(data => {
            console.log(data)
            return res.status(200).json({message: "Sent successful"})
        }).catch(err => {
            return res.status(400).json({error: err.message})
        })
    } catch (err) {
        return res.status(400).json(err.message)
    }
}

async function handleGetChatByUser(req, res){
    const userId = req.params.userId
    console.log(userId)
    const chats = await chatModel.find({
        $or: [
            { from: userId},
            { to: userId}    
        ]
    }).sort({ createdAt: 1 });

    const users = []

    for (const chat of chats) {
        const opponentId = chat.from === userId ? chat.to : chat.from;

        // Fetch the opponent user data using the opponentId
        const opponentUser = await userModel.findById(opponentId);
        if (opponentUser) {
            if (!users.some(user => user._id.toString() === opponentUser._id.toString())) {
                users.push(opponentUser);
            }
        }
    }

    return res.status(200).json(users)
}

module.exports = {
    handleGetChat,
    handleCreateChat,
    handleGetChatByUser
}