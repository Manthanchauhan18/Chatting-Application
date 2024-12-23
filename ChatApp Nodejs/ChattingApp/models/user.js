const {Schema , model} = require('mongoose')
const { randomBytes, createHmac } = require('crypto');
const { type } = require('os');

const userSchema = new Schema({
    fullname : {
        type: String,
        require: true
    },
    email : {
        type: String,
        require: true,
        unique: true
    },
    salt : {
        type: String,
    },
    password : {
        type: String,
        require: true
    },
    status: {
        type: String,
        require: true
    },
    profileImage:{
        type: String
    }
},{timestamps: true})

userSchema.pre('save', async function(next){
    const user = this;
    if(!user.isModified("password")) return;
    const salt = randomBytes(16).toString();
    const hashedPassword = createHmac("sha256", salt).update(user.password).digest('hex')
    this.salt = salt
    this.password = hashedPassword

    next()

})

userSchema.static("matchedPasswordHashed", async function(email, password){
    const user = await this.findOne({email})
    if(!user) throw new Error('Invalid username and password');
    const salt = user.salt
    const hashedPassword = user.password
    const userProvidedHash = createHmac("sha256", salt).update(password).digest('hex')
    console.log(salt, userProvidedHash)
    if(hashedPassword !== userProvidedHash) throw new Error('Invalid password');
    return user
})

userSchema.static("updatePassword", async function(email, newpassword){
    const user = await this.findOne({email})
    if(!user) throw new Error('User not found');
    const salt = user.salt
    const hashedPassword = createHmac("sha256", salt).update(newpassword).digest('hex')
    user.salt = salt
    user.password = hashedPassword
    await user.save()
    return user
})

const userModel = model('User', userSchema)

module.exports = userModel