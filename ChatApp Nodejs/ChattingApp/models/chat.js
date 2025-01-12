const {Schema, model} = require('mongoose')

const chatSchema = new Schema({
    message: {
        type: String,
        require: true
    },
    from: {
        type: String,
        require: true
    },
    to: {
        type: String,
        require: true
    },
    read: {
        type: Boolean,
        default: false
    }
},{timestamps: true})

const chatModel = model('Chats', chatSchema)

module.exports = chatModel