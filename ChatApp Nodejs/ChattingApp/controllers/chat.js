const chatModel = require('../models/chat')
const userModel = require('../models/user')

async function handleGetChat(req, res){
    const {from , to} = req.query
    const unreadMessList = req.body
    console.log(unreadMessList)

    const chats = await chatModel.find({
        $or: [
            { from: from, to: to },
            { from: to, to: from }
        ]
    }).sort({ createdAt: 1 });

    chats.forEach(async element => {
        console.log(element)
        await chatModel.findByIdAndUpdate(element._id, {read: true})
    });

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
            return res.status(200).json(data)
        }).catch(err => {
            return res.status(400).json({error: err.message})
        })
    } catch (err) {
        return res.status(400).json(err.message)
    }
}

async function handleGetChatByUser(req, res) {
    const userId = req.params.userId;

    const chats = await chatModel.find({
        $or: [
            { from: userId },
            { to: userId }
        ]
    }).sort({ createdAt: 1 });

    const users = [];

    for (const chat of chats) {
        const opponentId = chat.from === userId ? chat.to : chat.from;
        const opponentUser = await userModel.findById(opponentId);
        
        if (opponentUser) {
            if (!users.some(user => user._id.toString() === opponentUser._id.toString())) {
                const lastMessage = await chatModel.findOne({
                    $or: [
                        { from: opponentId, to: userId },
                        { from: userId, to: opponentId }
                    ]
                }).sort({ createdAt: -1 }).limit(1);

                const unreadCount = await chatModel.countDocuments({
                    from: opponentId,
                    to: userId,
                    read: false
                });
                // console.log(unreadCount)
                const unreadMess = await chatModel.find({
                    from: opponentId,
                    to: userId,
                    read: false
                })
                console.log(unreadMess)

                users.push({
                    _id: opponentUser._id,
                    fullname: opponentUser.fullname,
                    email: opponentUser.email,
                    status: opponentUser.status,
                    profileImage: opponentUser.profileImage,
                    lastMessage: lastMessage ? lastMessage.message : null,
                    lastMessageTime: lastMessage ? lastMessage.createdAt : null,
                    unreadMessCount: unreadCount,
                    unreadMess: unreadMess
                });
            }
        }
    }

    users.sort((a, b) => {
        const timeA = a.lastMessageTime ? new Date(a.lastMessageTime) : new Date(0);
        const timeB = b.lastMessageTime ? new Date(b.lastMessageTime) : new Date(0);
        
        return timeB - timeA;
    });

    return res.status(200).json(users);
}



async function handleDeleteChatMessages(req, res){
    const messageList = req.body
    if (messageList && messageList.length > 0) {
        try {

            await messageList.forEach(async element => {
                console.log(element)
                const messId = element._id;
                await chatModel.findByIdAndDelete(messId);
            });
            
            return res.status(200).json({ message: "Messages deleted successfully" });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ message: "Error deleting messages", error: error.message });
        }
    } else {
        return res.status(400).json({ message: "No messages provided to delete" });
    } 
    
}

async function handleDeleteChatByUser(req, res){
    const userId = req.params.userId
    const userList = req.body
    // console.log(userId)
    // console.log(userList)
    if (userList && userList.length > 0) {
        try {
            await userList.forEach(async element => {
                const to = element

                const chats = await chatModel.find({
                    $or: [
                        { from: userId, to: to }, 
                        { from: to, to: userId } 
                    ]
                }).sort({ createdAt: 1 });
                // console.log(chats)
                chats.forEach(async element => {
                    await chatModel.findByIdAndDelete(element._id)
                });

            });
            
            return res.status(200).json({ message: "Chat deleted successfully" });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ message: "Error deleting messages", error: error.message });
        }
    } else {
        return res.status(400).json({ message: "No messages provided to delete" });
    } 
}



module.exports = {
    handleGetChat,
    handleCreateChat,
    handleGetChatByUser,
    handleDeleteChatMessages,
    handleDeleteChatByUser
}