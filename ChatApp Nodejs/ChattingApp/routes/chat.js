const {Router} = require('express')
const { handleGetChat, handleCreateChat, handleGetChatByUser, handleDeleteChatMessages } = require('../controllers/chat')

const router = Router()

router.get('/',handleGetChat)
router.get('/user/:userId',handleGetChatByUser)
router.post('/create',handleCreateChat)
router.post('/deleteMessages',handleDeleteChatMessages)

module.exports = router