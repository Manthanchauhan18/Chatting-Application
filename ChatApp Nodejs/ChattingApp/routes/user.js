const {Router} = require('express')
const { handlePostUserLogin, handlePostUserRegistration, handleGetUsers, handleGetUserById, handleUpdateUserProfile, handleUpdateUserPassword } = require('../controllers/user')

const router = Router()
console.log("inside router")

router.post('/login', handlePostUserLogin)
router.post('/signup', handlePostUserRegistration)
router.get('/', handleGetUsers)
router.get('/:userId', handleGetUserById)
router.patch('/update/:userId', handleUpdateUserProfile)
router.post('/updatePassword',handleUpdateUserPassword)

module.exports = router