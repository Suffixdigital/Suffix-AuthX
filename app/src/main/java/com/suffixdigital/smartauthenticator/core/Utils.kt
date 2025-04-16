package com.suffixdigital.smartauthenticator.core

import android.content.Context
import android.text.InputFilter
import android.util.Log
import android.widget.Toast
import java.util.regex.Pattern

const val TAG = "AppTag"
const val EMPTY_STRING = ""

fun logErrorMessage(
    errorMessage: String
) = Log.e(TAG, errorMessage)

fun showToastMessage(
    context: Context,
    message: String
) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

/******************************************************************************
 * [inputFilter] is a instance of [InputFilter]. Here for 'email' type of [com.google.android.material.textfield.TextInputEditText] which has upper case input from soft keyboard
 * this function are convert all inputs of uppercase characters to lower case.
 * We just need to apply this [inputFilter] with required [com.google.android.material.textfield.TextInputEditText]
 * Now there is no effect of 'shift key' is ON/OFF when user input data to 'Email'
 * ex: 'binding.etEmailId.filters = arrayOf(inputFilter)'
 *****************************************************************************/
val inputFilter = InputFilter { source, start, end, _, _, _ ->
    val builder = StringBuilder()
    for (i in start until end) {
        val c = source[i]
        if (Character.isUpperCase(c)) {
            builder.append(Character.toLowerCase(c))
        } else {
            builder.append(c)
        }
    }
    builder.toString()
}

const val phonePattern: String = "(###) ###-####"
val passwordRegex: Pattern = Pattern.compile(
    "^" + "(?=.*\\d)" +    //at least 1 digit
            "(?=.*[a-z])" +         //at least 1 lower case letter
            "(?=.*[A-Z])" +         //at least 1 upper case letter
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=.*[@#$%^&+=])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{8,32}" +             //at least 8 characters and maximum 32 characters
            "$"
)

const val offensiveWords: String =
    "4r5e|5h1t|5hit|a55|anal|anus|ar5e|arrse|arse|ass|ass-fucker|asses|assfucker|assfukka|asshole|assholes|asswhole|a_s_s|b!tch|b00bs|b17ch|b1tch|ballbag|balls|ballsack|bastard|beastial|beastiality|bellend|bestial|bestiality|bi\\+ch|biatch|bitch|bitcher|bitchers|bitches|bitchin|bitching|bloody|blow job|blowjob|blowjobs|boiolas|bollock|bollok|boner|boob|boobs|booobs|boooobs|booooobs|booooooobs|breasts|buceta|bugger|bum|bunny fucker|butt|butthole|buttmuch|buttplug|c0ck|c0cksucker|carpet muncher|cawk|chink|cipa|cl1t|clit|clitoris|clits|cnut|cock|cock-sucker|cockface|cockhead|cockmunch|cockmuncher|cocks|cocksuck|cocksucked|cocksucker|cocksucking|cocksucks|cocksuka|cocksukka|cok|cokmuncher|coksucka|coon|cox|crap|cum|cummer|cumming|cums|cumshot|cunilingus|cunillingus|cunnilingus|cunt|cuntlick|cuntlicker|cuntlicking|cunts|cyalis|cyberfuc|cyberfuck|cyberfucked|cyberfucker|cyberfuckers|cyberfucking|d1ck|damn|dick|dickhead|dildo|dildos|dink|dinks|dirsa|dlck|dog-fucker|doggin|dogging|donkeyribber|doosh|duche|dyke|ejaculate|ejaculated|ejaculates|ejaculating|ejaculatings|ejaculation|ejakulate|f u c k|f u c k e r|f4nny|fag|fagging|faggitt|faggot|faggs|fagot|fagots|fags|fanny|fannyflaps|fannyfucker|fanyy|fatass|fcuk|fcuker|fcuking|feck|fecker|felching|fellate|fellatio|fingerfuck|fingerfucked|fingerfucker|fingerfuckers|fingerfucking|fingerfucks|fistfuck|fistfucked|fistfucker|fistfuckers|fistfucking|fistfuckings|fistfucks|flange|fook|fooker|fuck|fucka|fucked|fucker|fuckers|fuckhead|fuckheads|fuckin|fucking|fuckings|fuckingshitmotherfucker|fuckme|fucks|fuckwhit|fuckwit|fudge packer|fudgepacker|fuk|fuker|fukker|fukkin|fuks|fukwhit|fukwit|fux|fux0r|f_u_c_k|gangbang|gangbanged|gangbangs|gaylord|gaysex|goatse|God|god-dam|god-damned|goddamn|goddamned|hardcoresex|hell|heshe|hoar|hoare|hoer|homo|hore|horniest|horny|hotsex|jack-off|jackoff|jap|jerk-off|jism|jiz|jizm|jizz|kawk|knob|knobead|knobed|knobend|knobhead|knobjocky|knobjokey|kock|kondum|kondums|kum|kummer|kumming|kums|kunilingus|l3i\\+ch|l3itch|labia|lust|lusting|m0f0|m0fo|m45terbate|ma5terb8|ma5terbate|masochist|master-bate|masterb8|masterbat*|masterbat3|masterbate|masterbation|masterbations|masturbate|mo-fo|mof0|mofo|mothafuck|mothafucka|mothafuckas|mothafuckaz|mothafucked|mothafucker|mothafuckers|mothafuckin|mothafucking|mothafuckings|mothafucks|mother fucker|motherfuck|motherfucked|motherfucker|motherfuckers|motherfuckin|motherfucking|motherfuckings|motherfuckka|motherfucks|muff|mutha|muthafecker|muthafuckker|muther|mutherfucker|n1gga|n1gger|nazi|nigg3r|nigg4h|nigga|niggah|niggas|niggaz|nigger|niggers|nob|nob jokey|nobhead|nobjocky|nobjokey|numbnuts|nutsack|orgasim|orgasims|orgasm|orgasms|p0rn|pawn|pecker|penis|penisfucker|phonesex|phuck|phuk|phuked|phuking|phukked|phukking|phuks|phuq|pigfucker|pimpis|piss|pissed|pisser|pissers|pisses|pissflaps|pissin|pissing|pissoff|poop|porn|porno|pornography|pornos|prick|pricks|pron|pube|pusse|pussi|pussies|pussy|pussys|rectum|retard|rimjaw|rimming|s hit|s.o.b.|sadist|schlong|screwing|scroat|scrote|scrotum|semen|sex|sh!\\+|sh!t|sh1t|shag|shagger|shaggin|shagging|shemale|shi\\+|shit|shitdick|shite|shited|shitey|shitfuck|shitfull|shithead|shiting|shitings|shits|shitted|shitter|shitters|shitting|shittings|shitty|skank|slut|sluts|smegma|smut|snatch|son-of-a-bitch|spac|spunk|s_h_i_t|t1tt1e5|t1tties|teets|teez|testical|testicle|tit|titfuck|tits|titt|tittie5|tittiefucker|titties|tittyfuck|tittywank|titwank|tosser|turd|tw4t|twat|twathead|twatty|twunt|twunter|v14gra|v1gra|vagina|viagra|vulva|w00se|wang|wank|wanker|wanky|whoar|whore|willies|willy|xrated|xxx|analannie|analsex|arsehole|assbagger|assblaster|assclown|asscowboy|assfuck|asshat|asshore|assjockey|asskiss|asskisser|assklown|asslick|asslicker|asslover|assman|assmonkey|assmunch|assmuncher|asspacker|asspirate|asspuppies|assranger|asswhore|asswipe|badfuck|bigbastard|bigbutt|bitchez|bitchslap|bitchy|bondage|bong|boobies|booty|bootycall|bullshit|bumblefuck|bumfuck|buttbang|butt-bang|buttface|buttfuck|butt-fuck|buttfucker|butt-fucker|buttfuckers|butt-fuckers|butthead|buttman|buttmunch|buttmuncher|buttpirate|buttstain|chinky|cockblock|cockblocker|cockcowboy|cockfight|cockknob|cocklicker|cocklover|cocknob|cockqueen|cockrider|cocksman|cocksmith|cocksmoker|cocksucer|cocktease|crackwhore|crack-whore|cunteyed|cuntfuck|cuntfucker|cuntsucker|datnigga|dickbrain|dickforbrains|dickless|dicklick|dicklicker|dickman|dickwad|dickweed|dipshit|dripdick|dumbass|dumbbitch|dumbfuck|eatballs|eatpussy|facefucker|fastfuck|fatfuck|fatfucker|fckcum|felatio|fisting|footfuck|footfucker|foreskin|freakfuck|freakyfucker|freefuck|fuckable|fuckbag|fuckbuddy|fuckedup|fuckface|fuckfest|fuckfreak|fuckfriend|fuckher|fuckina|fuckingbitch|fuckinnuts|fuckinright|fuckit|fuckknob|fuckmehard|fuckmonkey|fuckoff|fuckpig|fucktard|fuckwhore|fuckyou|funfuck|gangbanger|gaymuthafuckinwhore|godammit|goddamit|goddammit|goddamnes|goddamnit|goddamnmuthafucker|goldenshower|handjob|headfuck|hiscock|horseshit|hotpussy|iblowu|jackshit|jerkoff|jizzim|jizzum|limpdick|mastabate|mastabater|masterblaster|mastrabator|masturbating|motherlovebone|nastybitch|nastywhore|nigg|niggaracci|niggard|niggarded|niggarding|niggardliness|niggardliness's|niggardly|niggards|niggard's|niggerhead|niggerhole|nigger's|niggle|niggled|niggles|niggling|nigglings|niggor|niggur|nofuckingway|nutfucker|peni5|penile|penises|pindick|pornflick|pornking|pornprincess|puntang|puss|pussie|pussyeater|pussyfucker|pussylicker|pussylips|pussylover|pussypounder|rentafuck|sandnigger|sexwhore|shitcan|shiteater|shitface|shitfaced|shitfit|shitforbrains|shitfucker|shithapens|shithappens|shithouse|shitlist|shitola|shitoutofluck|shitstain|shortfuck|skankbitch|skankfuck|skankwhore|skankybitch|skankywhore|slutwhore|snigger|sniggered|sniggering|sniggers|snigger's|snownigger|sonofabitch|sonofbitch|spaghettinigger|stupidfuck|stupidfucker|suckdick|suckmyass|suckmydick|suckmytit|timbernigger|titbitnipply|titfucker|titfuckin|titjob|titlicker|titlover|tittie|titty|twobitwhore|unfuckable|upthebutt|wanking|whiskeydick|whiskydick|whitenigger|whorefucker|whorehouse|williewanker"


/******************************************************************************
 * [checkOffensiveWord] function is used to identify 'offensive' words from user input data in [com.google.android.material.textfield.TextInputEditText]
 * Ex: "This is an offensive words ass": userInput
 * check offensive words in whole sentence using [offensiveWords] pattern and found any words then return 'true' otherwise 'false'
 * @param customInput: userInput data
 * @return Boolean: True:  If offensive words found in [customInput]
 *                  False: otherwise
 *****************************************************************************/
fun checkOffensiveWord(customInput: String): Boolean =
    offensiveWords.toRegex(setOf(RegexOption.IGNORE_CASE)).containsMatchIn(customInput)


/******************************************************************************
 * [trimPhoneNumber] is used to remove regex from phone number. When phone number needs to use in
 * Retrofit API call then need to regex from phoneNumber
 *
 * @param phoneNumber with regex format Phone Number
 * @return String: removed regex from phone number
 *****************************************************************************/
fun trimPhoneNumber(phoneNumber: String): String {
    return phoneNumber.replace(Regex("[()\\-\\s]"), "").trim()
}

/**
 * [isPasswordPatternValid] is used to validate inserted/deleted password in [com.google.android.material.textfield.TextInputEditText] field.
 *
 * @param password String format password
 * @return boolean : True  -> Password follow the Regex
 *                   False -> Password not follow required Regex
 */
fun isPasswordPatternValid(password: String): Boolean {
    return passwordRegex.matcher(password).matches()
}
