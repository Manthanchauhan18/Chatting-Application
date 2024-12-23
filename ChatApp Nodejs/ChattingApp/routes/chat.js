const {Router} = require('express')
const { handleGetChat, handleCreateChat, handleGetChatByUser, handleDeleteChatMessages, handleDeleteChatByUser } = require('../controllers/chat')

const router = Router()

router.post('/',handleGetChat)
router.get('/user/:userId',handleGetChatByUser)
router.post('/create',handleCreateChat)
router.post('/deleteMessages',handleDeleteChatMessages)
router.post('/delete/:userId',handleDeleteChatByUser)

module.exports = router