let ws;
//Get incoming messages container
const incoming_messages = document.getElementById('incoming_messages')

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
    ws = new WebSocket("wss://" + location.host + "/freechat")

    ws.onmessage = function(data) {
        incomingMessage(data.data)
    }

    // ws.onconnect = e => {
    //
    // }

    ws.ondisconnect = e => {
        // console.log(e)
        connect()
    }

    setConnected(true)
}

//Disconnect from the chat.
function disconnect() {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
    // console.log("Websocket is in disconnected state");
}

//When the window is closed disconnect
window.onclose = e => {
    disconnect();
}

//When the tab is out of focus disconnect from the chat
window.onblur = e => {
    disconnect()
}


//When the tab comes back into focus connect
window.onfocus = e => {
    connect()
}

function sendData() {
    const message = document.getElementById('message')
    const userid = document.getElementById('userid')

    // console.log(userid.value + " sending => " + message.value)
    const data = JSON.stringify({
        'userid' : userid.value,
        'message' : message.value
    })

    // console.log("Sending payload")
    // console.log(data);


    ws.send(data);

    message.value = "";
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
    //Grab the send button
    document.getElementById('send').onclick = sendData

    //Connect to the chat socket
    connect()

    //Scroll all the way down on the messages
    setTimeout(()=>{
        incoming_messages.scrollTop = incoming_messages.scrollHeight;
    },200)


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
        if(display_chat){
            setTimeout(()=>{
                incoming_messages.scrollTop = incoming_messages.scrollHeight;
            },200)
        }
    }
}

if(message) {
    message.onkeyup = e => {
        const key = e.key
        if (key === 'Enter') {
            e.preventDefault();
            sendData()
            message.value = ""; //Clear the message
        }
    }
}
