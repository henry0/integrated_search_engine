/**
 * 
 */
var nodemailer = require('nodemailer');

// create reusable transporter object using the default SMTP transport
var transporter = nodemailer.createTransport('smtps://henry.0th%40gmail.com:_mypwis9178@smtp.gmail.com');

// setup e-mail data with unicode symbols
var mailOptions = {
    from: '"김현기 " <henry8th@naver.com>', // sender address
    to: 'henry.0th@gmail.com', // list of receivers
    subject: 'Hello ✔ test send node mailer', // Subject line
    text: 'Hello world 🐴', // plaintext body
    html: '<b>Hello world 🐴</b>' // html body
};

// send mail with defined transport object
transporter.sendMail(mailOptions, function(error, info){
    if(error){
        return console.log(error);
    }
    console.log('Message sent: ' + info.response);
});