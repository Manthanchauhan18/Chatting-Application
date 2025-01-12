const userModel = require('../models/user')
const multer = require('multer')
const path = require('path')

const storage = multer.diskStorage({
    destination:function(req, file, cb){
        cb(null, path.resolve('./public/users'))
    },
    filename: function(req, file, cb){
        const filename = `${Date.now()}-${file.originalname}`
        cb(null, filename)
    }
})

const upload = multer({storage: storage})

async function handlePostUserLogin(req , res) {
    const {email, password} = req.body
    // console.log(email, password)
    try{
        const user = await userModel.matchedPasswordHashed(email, password)
        return res.status(200).json(user)
    }catch(err){
        return res.status(400).json({error: err.message}) 
    }
}

async function handlePostUserRegistration(req , res){
    upload.single('profileImage')(req, res, async(err) => {
        
        const { fullname, email, password, status} = req.body
        const isEmailExist = await userModel.findOne({email})
        if(isEmailExist) return res.status(200).json({error: "email already registered"});
        // console.log(req.file)
        // console.log(req)
        try {
            if(status === "" && req.file === undefined){
                await userModel.create({
                    fullname,
                    email,
                    password,
                    status: "Hey there! I am using ChatApp",
                    profileImage: ""
                })
                return res.status(200).json({message: "successfuly created"})
            }else if(status === "" && req.file !== undefined){
                await userModel.create({
                    fullname,
                    email,
                    password,
                    status: "Hey there! I am using ChatApp",
                    profileImage: `/users/${req.file.filename}`
                })
                return res.status(200).json({message: "successfuly created"})
            }else if(status !== "" && req.file === undefined){
                await userModel.create({
                    fullname,
                    email,
                    password,
                    status,
                    profileImage: ""
                })
                return res.status(200).json({message: "successfuly created"})
            }else{
                await userModel.create({
                    fullname,
                    email,
                    password,
                    status,
                    profileImage: `/users/${req.file.filename}`
                })
                return res.status(200).json({message: "successfuly created"})
            }
            
        } catch (err) {
            return res.status(400).json({error: err.message})
        }

    })
    
    
}

async function handleGetUsers(req, res){
    const users = await userModel.find({})
    return res.status(200).json(users)
}

async function handleGetUserById(req, res){
    const userId = req.params.userId
    await userModel.findById(userId).then(data => {
        return res.status(200).json(data)
    }).catch(err => {
        return res.status(400).json({error: err.message})
    })
}

async function handleUpdateUserProfile(req, res){
    const userId = req.params.userId
    const userUpdatedData = req.body
    await userModel.findByIdAndUpdate(userId, {...userUpdatedData}).then(data => {
        return res.status(200).json({message: "User updated"})
    }).catch(err => {
        return res.status(400).json({error: err.message})
    })

}

async function handleUpdateUserPassword(req, res){
    const {userId, oldPassword, newPassword} = req.body
    // console.log(userId, oldPassword, newPassword)
    const user = await userModel.findById(userId)
    try {
        await userModel.matchedPasswordHashed(user.email, oldPassword).then(async data => {
            user.password = newPassword
            await user.save()
            return res.status(200).json({message: "password updated"})
        }).catch(err => {
            return res.status(400).json({error: err.message})
        })
    } catch (error) {
        return res.status(400).json({error: error.message})
    }

}

module.exports = {
    handlePostUserLogin,
    handlePostUserRegistration,
    handleGetUsers,
    handleGetUserById,
    handleUpdateUserProfile,
    handleUpdateUserPassword
}