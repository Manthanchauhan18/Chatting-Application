const {Router} = require('express')
const { handlePostUserLogin, handlePostUserRegistration, handleGetUsers } = require('../controllers/user')

const router = Router()
console.log("inside router")

router.post('/login', handlePostUserLogin)
router.post('/signup', handlePostUserRegistration)
router.get('/', handleGetUsers)

module.exports = router