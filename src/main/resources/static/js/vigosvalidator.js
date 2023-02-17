
//Check the password and validate based on the passed rules object
/*
 * Call like this:
 *          checkPassword(password,{
 *              min : number,
 *              max : number,
 *              hasSpecial : number,
 *              hasNumber : number
 *          }
 */
const checkPassword = (pass,rules,modal,modalbody) => {
    //If he password is null
    if(pass == null){
        errorsBody.innerText = "Please provide a password"
        errorsModal.show()
    }

    //Weather the length is below the min
    const min = (pass, l) => pass.length >= l

    //Weather the length is below the max
    const max = (pass, l) => pass.length <= l


    //Special character list
    const special = ['!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '-', '=', '+', ',', '<', '>', '?', '"', '\'', '[', ']', '{', '}', '|', '~', '`', '\\', '/']

    //Weather there's (count) number of special character
    const hasSpecial = (pass, count) => hasEnough = pass.value.split('').filter(c => special.includes(c)).length >= count

    //Check for minimum number of numeric digits
    const hasNumber = (pass, count) => pass.value.match(/\d/g)?.length >= count || false

    //Array to store the errors we encounter during the following checks
    let errors = []

    if('min' in rules){
        //If we have to check for a minimum number of characters
        if(min(password,rules.min)){ //And the check fails
            errors.push("Your password must be at least " + rules.min + " characters long.")
            console.log(errors[errors.length - 1])
        }
    }

    if('max' in rules){
        //If we need to check for a max number of characters
        if(max(password,rules.max)){ //And the check fails
            errors.push("Your password must be at most " + rules.max + " characters long.")
            console.log(errors[errors.length - 1])
        }
    }

    if('hasSpecial' in rules){
        //If we have to check for special characters
        if(!hasSpecial(password,rules.hasSpecial)){ //And the check fails
            errors.push("Your password must have at least " + rules.hasSpecial + " special characters.")
            console.log(errors[errors.length - 1])
        }
    }

    if('hasNumber' in rules){ //If we need to check for numeric characters
        if(!hasNumber(password,rules.hasNumber)){ //And the check fails
            errors.push("Your password must have at least " + rules.hasNumber + " numeric characters.")
            console.log(errors[errors.length - 1])
        }
    }

    //If we have errors
    if(errors.length > 0){
        //Clear the modal body
        modalbody.innerText = ""

        //Add the errors to it
        errors.forEach(e=>{
            const errItem = document.createElement('div')
            errItem.innerText = e
            errItem.classList.add('mt-1')
            modalbody.appendChild(errItem)
        })

        //And show the modal
        modal.show()

        return false
    }else{ //Otherwise
        return true
    }
}


//If you're sensitive to profanity skip the next bit. For educational purposes only.
const bad_words = {"4r5e": 1, "5h1t": 1, "5hit": 1, "a55": 1, "anal": 1, "anus": 1, "ar5e": 1, "arrse": 1, "arse": 1, "ass": 1, "ass-fucker": 1, "asses": 1, "assfucker": 1, "assfukka": 1, "asshole": 1, "assholes": 1, "asswhole": 1, "a_s_s": 1, "b!tch": 1, "b00bs": 1, "b17ch": 1, "b1tch": 1, "ballbag": 1, "balls": 1, "ballsack": 1, "bastard": 1, "beastial": 1, "beastiality": 1, "bellend": 1, "bestial": 1, "bestiality": 1, "bi+ch": 1, "biatch": 1, "bitch": 1, "bitcher": 1, "bitchers": 1, "bitches": 1, "bitchin": 1, "bitching": 1, "bloody": 1, "blow job": 1, "blowjob": 1, "blowjobs": 1, "boiolas": 1, "bollock": 1, "bollok": 1, "boner": 1, "boob": 1, "boobs": 1, "booobs": 1, "boooobs": 1, "booooobs": 1, "booooooobs": 1, "breasts": 1, "buceta": 1, "bugger": 1, "bum": 1, "bunny fucker": 1, "butt": 1, "butthole": 1, "buttmuch": 1, "buttplug": 1, "c0ck": 1, "c0cksucker": 1, "carpet muncher": 1, "cawk": 1, "chink": 1, "cipa": 1, "cl1t": 1, "clit": 1, "clitoris": 1, "clits": 1, "cnut": 1, "cock": 1, "cock-sucker": 1, "cockface": 1, "cockhead": 1, "cockmunch": 1, "cockmuncher": 1, "cocks": 1, "cocksuck": 1, "cocksucked": 1, "cocksucker": 1, "cocksucking": 1, "cocksucks": 1, "cocksuka": 1, "cocksukka": 1, "cok": 1, "cokmuncher": 1, "coksucka": 1, "coon": 1, "cox": 1, "crap": 1, "cum": 1, "cummer": 1, "cumming": 1, "cums": 1, "cumshot": 1, "cunilingus": 1, "cunillingus": 1, "cunnilingus": 1, "cunt": 1, "cuntlick": 1, "cuntlicker": 1, "cuntlicking": 1, "cunts": 1, "cyalis": 1, "cyberfuc": 1, "cyberfuck": 1, "cyberfucked": 1, "cyberfucker": 1, "cyberfuckers": 1, "cyberfucking": 1, "d1ck": 1, "damn": 1, "dick": 1, "dickhead": 1, "dildo": 1, "dildos": 1, "dink": 1, "dinks": 1, "dirsa": 1, "dlck": 1, "dog-fucker": 1, "doggin": 1, "dogging": 1, "donkeyribber": 1, "doosh": 1, "duche": 1, "dyke": 1, "ejaculate": 1, "ejaculated": 1, "ejaculates": 1, "ejaculating": 1, "ejaculatings": 1, "ejaculation": 1, "ejakulate": 1, "f u c k": 1, "f u c k e r": 1, "f4nny": 1, "fag": 1, "fagging": 1, "faggitt": 1, "faggot": 1, "faggs": 1, "fagot": 1, "fagots": 1, "fags": 1, "fanny": 1, "fannyflaps": 1, "fannyfucker": 1, "fanyy": 1, "fatass": 1, "fcuk": 1, "fcuker": 1, "fcuking": 1, "feck": 1, "fecker": 1, "felching": 1, "fellate": 1, "fellatio": 1, "fingerfuck": 1, "fingerfucked": 1, "fingerfucker": 1, "fingerfuckers": 1, "fingerfucking": 1, "fingerfucks": 1, "fistfuck": 1, "fistfucked": 1, "fistfucker": 1, "fistfuckers": 1, "fistfucking": 1, "fistfuckings": 1, "fistfucks": 1, "flange": 1, "fook": 1, "fooker": 1, "fuck": 1, "fucka": 1, "fucked": 1, "fucker": 1, "fuckers": 1, "fuckhead": 1, "fuckheads": 1, "fuckin": 1, "fucking": 1, "fuckings": 1, "fuckingshitmotherfucker": 1, "fuckme": 1, "fucks": 1, "fuckwhit": 1, "fuckwit": 1, "fudge packer": 1, "fudgepacker": 1, "fuk": 1, "fuker": 1, "fukker": 1, "fukkin": 1, "fuks": 1, "fukwhit": 1, "fukwit": 1, "fux": 1, "fux0r": 1, "f_u_c_k": 1, "gangbang": 1, "gangbanged": 1, "gangbangs": 1, "gaylord": 1, "gaysex": 1, "goatse": 1, "God": 1, "god-dam": 1, "god-damned": 1, "goddamn": 1, "goddamned": 1, "hardcoresex": 1, "hell": 1, "heshe": 1, "hoar": 1, "hoare": 1, "hoer": 1, "homo": 1, "hore": 1, "horniest": 1, "horny": 1, "hotsex": 1, "jack-off": 1, "jackoff": 1, "jap": 1, "jerk-off": 1, "jism": 1, "jiz": 1, "jizm": 1, "jizz": 1, "kawk": 1, "knob": 1, "knobead": 1, "knobed": 1, "knobend": 1, "knobhead": 1, "knobjocky": 1, "knobjokey": 1, "kock": 1, "kondum": 1, "kondums": 1, "kum": 1, "kummer": 1, "kumming": 1, "kums": 1, "kunilingus": 1, "l3i+ch": 1, "l3itch": 1, "labia": 1, "lust": 1, "lusting": 1, "m0f0": 1, "m0fo": 1, "m45terbate": 1, "ma5terb8": 1, "ma5terbate": 1, "masochist": 1, "master-bate": 1, "masterb8": 1, "masterbat*": 1, "masterbat3": 1, "masterbate": 1, "masterbation": 1, "masterbations": 1, "masturbate": 1, "mo-fo": 1, "mof0": 1, "mofo": 1, "mothafuck": 1, "mothafucka": 1, "mothafuckas": 1, "mothafuckaz": 1, "mothafucked": 1, "mothafucker": 1, "mothafuckers": 1, "mothafuckin": 1, "mothafucking": 1, "mothafuckings": 1, "mothafucks": 1, "mother fucker": 1, "motherfuck": 1, "motherfucked": 1, "motherfucker": 1, "motherfuckers": 1, "motherfuckin": 1, "motherfucking": 1, "motherfuckings": 1, "motherfuckka": 1, "motherfucks": 1, "muff": 1, "mutha": 1, "muthafecker": 1, "muthafuckker": 1, "muther": 1, "mutherfucker": 1, "n1gga": 1, "n1gger": 1, "nazi": 1, "nigg3r": 1, "nigg4h": 1, "nigga": 1, "niggah": 1, "niggas": 1, "niggaz": 1, "nigger": 1, "niggers": 1, "nob": 1, "nob jokey": 1, "nobhead": 1, "nobjocky": 1, "nobjokey": 1, "numbnuts": 1, "nutsack": 1, "orgasim": 1, "orgasims": 1, "orgasm": 1, "orgasms": 1, "p0rn": 1, "pawn": 1, "pecker": 1, "penis": 1, "penisfucker": 1, "phonesex": 1, "phuck": 1, "phuk": 1, "phuked": 1, "phuking": 1, "phukked": 1, "phukking": 1, "phuks": 1, "phuq": 1, "pigfucker": 1, "pimpis": 1, "piss": 1, "pissed": 1, "pisser": 1, "pissers": 1, "pisses": 1, "pissflaps": 1, "pissin": 1, "pissing": 1, "pissoff": 1, "poop": 1, "porn": 1, "porno": 1, "pornography": 1, "pornos": 1, "prick": 1, "pricks": 1, "pron": 1, "pube": 1, "pusse": 1, "pussi": 1, "pussies": 1, "pussy": 1, "pussys": 1, "rectum": 1, "retard": 1, "rimjaw": 1, "rimming": 1, "s hit": 1, "s.o.b.": 1, "sadist": 1, "schlong": 1, "screwing": 1, "scroat": 1, "scrote": 1, "scrotum": 1, "semen": 1, "sex": 1, "sh!+": 1, "sh!t": 1, "sh1t": 1, "shag": 1, "shagger": 1, "shaggin": 1, "shagging": 1, "shemale": 1, "shi+": 1, "shit": 1, "shitdick": 1, "shite": 1, "shited": 1, "shitey": 1, "shitfuck": 1, "shitfull": 1, "shithead": 1, "shiting": 1, "shitings": 1, "shits": 1, "shitted": 1, "shitter": 1, "shitters": 1, "shitting": 1, "shittings": 1, "shitty": 1, "skank": 1, "slut": 1, "sluts": 1, "smegma": 1, "smut": 1, "snatch": 1, "son-of-a-bitch": 1, "spac": 1, "spunk": 1, "s_h_i_t": 1, "t1tt1e5": 1, "t1tties": 1, "teets": 1, "teez": 1, "testical": 1, "testicle": 1, "tit": 1, "titfuck": 1, "tits": 1, "titt": 1, "tittie5": 1, "tittiefucker": 1, "titties": 1, "tittyfuck": 1, "tittywank": 1, "titwank": 1, "tosser": 1, "turd": 1, "tw4t": 1, "twat": 1, "twathead": 1, "twatty": 1, "twunt": 1, "twunter": 1, "v14gra": 1, "v1gra": 1, "vagina": 1, "viagra": 1, "vulva": 1, "w00se": 1, "wang": 1, "wank": 1, "wanker": 1, "wanky": 1, "whoar": 1, "whore": 1, "willies": 1, "willy": 1, "xrated": 1, "xxx": 1};

/*
 * Check username for minimum and maximum length requirements and generates errors as required in the passed modal's body and displays the modal.
 */
const checkUsername = (username,minlength,maxlength,modalbody,modal) => {
    let errors = []

    const matchingBadWords = Object.keys(bad_words).filter(word=>username.value.toLowerCase().includes(word))

    //If the password is included in the list of bad words
    if(matchingBadWords.length > 0){
        errors.push("Password cannot contain profanity. Please don't include any profane language in the content for this website. It is against the terms of use.")
    }

    //Check the username for minimum length requirements
    if(username.value.length <= minlength){
        errors.push("Username must be at least 7 characters long.")
    }

    //Check the username for maximum length requirements
    if(username.value.length >= maxlength){
        errors.push("Username must be under 12 characters long")
    }

    //Check for errors
    if(errors.length === 0){ //If there are no errors
        return true
    }else{ //Otherwise
        //Clear the error modal's dialog body
        modalbody.innerText = ""

        //Generate the error messages
        errors.forEach(e=>{
            const errItem = document.createElement('div')
            errItem.innerText = e
            errItem.classList.add('mt-1')

            //Add the error items to the modal body
            modalbody.appendChild(errItem)
        })

        //Show the modal
        modal.show()

        return false
    }
}


/*
 * Check if an email is valid
 */

const isEmailValid = (email,modal,modalbody) => {

    //If the email is not valid validation based on chromium tests
    const valid =  String(email.value).toLowerCase().match(/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);

    if (!valid) {
        const errMsg = document.createElement('span')
        errMsg.innerText = "Please enter a valid email."

        //Clear the modal body
        modalbody.innerText = ""

        //Append the error message to the modal body
        modalbody.appendChild(errMsg)

        //Show the modal
        modal.show()

        return false
    }

    return true
}

/*
 *  Check that the person's name is not empty or a profane word
 */
const checkFullName = (first,last,modal,modalbody) => {
    //Container to hold all the errors.
    let errors = []

    //If first name is less than 1 character long
    if(first.value.length < 1){
        //Add an error
        errors.push("Your first name must be at least 1 character long.")
    }

    //Matching profanity in first name
    const matchingBadWordsFirstName = Object.keys(bad_words).filter(word=>first.value.toLowerCase().includes(word))

    //Profanity check first name
    if(matchingBadWordsFirstName.length > 0){
        errors.push("Your first name uses profanity which is against the terms of use of this website.")
    }

    //If the last name is less than 1 character long
    if(last.value.length < 1){
        //Add an error
        errors.push("Your last name must be at least 1 character long.")
    }

    //Matching profanity is last name
    const matchingBadWordsLastName = Object.keys(bad_words).filter(word=>last.value.toLowerCase().includes(word))

    //If the last name has profanity
    if(matchingBadWordsLastName.length > 0){
        errors.push("Your last name uses profanity which is against the terms of use of this website.")
    }

    //If there are no errors
    if(errors.length === 0){
        //Return true
        return true
    }else{
        //Clear the modal
        modalbody.innerText = ""

        //Generate error messages
        errors.forEach(err=>{

            //Create the error message
            const errMsg = document.createElement('div')

            //Attach the error text into the element
            errMsg.innerText = err

            //Attach the element to the modal's body
            modalbody.appendChild(errMsg)
        })

        //Display the error modal
        modal.show()

        return false
    }
}