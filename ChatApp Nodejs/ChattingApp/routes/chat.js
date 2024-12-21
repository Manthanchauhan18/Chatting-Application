const {Router} = require('express')
const { handleGetChat, handleCreateChat, handleGetChatByUser } = require('../controllers/chat')

const router = Router()

router.get('/',handleGetChat)
router.get('/user/:userId',handleGetChatByUser)
router.post('/create',handleCreateChat)

module.exports = router