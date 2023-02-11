let ws;
function setConnected(connected) {
    if(connected) {
        setTimeout(()=>{
            const userid = document.getElementById('userid')

            if(userid) {
                const useridMsg = JSON.stringify({
                    'userid': userid.value
                })

                ws.send(useridMsg)
            }
        },1500)
    }
}

function connect() {
    console.log("Connecting to ws")

    ws = new WebSocket("ws://" + location.host + "/freechat")

    ws.onmessage = function(data) {
        incomingMessage(data.data)
    }

    ws.onconnect = e => {
        console.log(e)
    }

    ws.ondisconnect = e => {
        console.log(e)
        connect()
    }

    setConnected(true)
}

function disconnect() {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
    console.log("Websocket is in disconnected state");
}

function sendData() {
    const message = document.getElementById('message')
    const userid = document.getElementById('userid')

    const data = JSON.stringify({
        'userid' : userid.value,
        'message' : message.value
    })

    console.log("Sending payload")
    console.log(data);


    ws.send(data);
}

function incomingMessage(message) {
    const incoming_messages = document.getElementById('incoming_messages')
    incoming_messages.appendChild(document.createElement("br"));
    const msg = document.createElement('div')
    msg.innerText = message
    incoming_messages.appendChild(msg)
    incoming_messages.scrollTo(0,msg.offsetTop)
}

(() => {
    // Array.from(document.getElementsByTagName("form")).forEach(form=>{
    //     form.onsubmit = e => e.preventDefault()
    // })

    // document.getElementById('send').onclick = sendData

    connect()

})();


const chat_icon = document.getElementById('chat_icon')
const main_chat = document.getElementById('main-chat')
const message = document.getElementById('message')

let display_chat = false

if(chat_icon) {
    chat_icon.onclick = e => {
        display_chat = !display_chat
        main_chat.style.display = display_chat ? 'grid' : 'none'
        main_chat.style.width = "fit-content";
        main_chat.style.height = "400px";
        main_chat.style.maxHeight = "400px";
        main_chat.style.overflowY = "scroll";
    }
}

if(message) {
    message.onkeyup = e => {
        const key = e.key
        console.log(e)
        if (key === 'Enter') {
            e.preventDefault();
            sendData()
            message.value = ""; //Clear the message
        }
    }
}

// setInterval(()=>{
//     //Every 10 seconds check the socket connection
// },10000)
