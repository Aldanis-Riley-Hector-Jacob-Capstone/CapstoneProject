let ws;
function setConnected(connected) {
    if(connected) {
        setTimeout(()=>{
            const userid = document.getElementById('userid')

            const useridMsg = JSON.stringify({
                'userid': userid.value
            })

            ws.send(useridMsg)
        },1500)
    }
}

function connect() {
    console.log("Connecting to ws")

    ws = new WebSocket("ws://" + location.host + "/freechat")

    ws.onmessage = function(data) {
        incomingMessage(data.data)
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
        'message' : message.value,
        'userid' : userid.value
    })


    ws.send(data);
}

function incomingMessage(message) {
    const incoming_messages = document.getElementById('incoming_messages')
    incoming_messages.appendChild(document.createElement("br"));
    const msg = document.createElement('span')
    msg.innerText = message
    incoming_messages.appendChild(msg)
}

(() => {
    Array.from(document.getElementsByTagName("form")).forEach(form=>{
        form.onsubmit = e => e.preventDefault()
    })

    document.getElementById('send').onclick = sendData

    connect()

})();


const chat_icon = document.getElementById('chat_icon')
const main_chat = document.getElementById('main-chat')
let display_chat = false
chat_icon.onclick = e => {
    display_chat = !display_chat
    main_chat.style.display = display_chat ? 'block' : 'none'
}