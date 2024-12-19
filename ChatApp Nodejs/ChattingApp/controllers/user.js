const userModel = require('../models/user')

async function handlePostUserLogin(req , res) {
    const {email, password} = req.body
    console.log(email, password)
    try{
        const user = await userModel.matchedPasswordHashed(email, password)
        return res.status(200).json(user)
    }catch(err){
        return res.status(400).json({error: err.message}) 
    }
}

async function handlePostUserRegistration(req , res){
    const { fullname, email, password, status} = req.body
    const isEmailExist = await userModel.findOne({email})
    if(isEmailExist) return res.status(200).json({error: "email already registered"});
    try {
        if(status === undefined){
            await userModel.create({
                fullname,
                email,
                password,
                status: "Hey there! I am using ChatApp"
            })
            return res.status(200).json({message: "successfuly created"})
        }else{
            await userModel.create({
                fullname,
                email,
                password,
                status
            })
            return res.status(200).json({message: "successfuly created"})
        }
        
    } catch (err) {
        return res.status(400).json({error: err.message})
    }
    
}

async function handleGetUsers(req, res){
    const users = await userModel.find({})
    return res.status(200).json(users)
}

module.exports = {
    handlePostUserLogin,
    handlePostUserRegistration,
    handleGetUsers
}