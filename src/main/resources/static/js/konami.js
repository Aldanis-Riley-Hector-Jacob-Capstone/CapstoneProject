(()=>{
    let expectedCode = [38,38,40,40,37,39,37,39,66,65]
    let currentCode = []
    document.addEventListener('keyup',event => {
        console.log("expected code: " + expectedCode)
        console.log("current code: " + currentCode)
        let win = () => {
            //Create a new window audio context
            const context = new window.AudioContext()

            //Create an oscillator with note A
            const oscA = context.createOscillator()
            oscA.type = 'sine'
            oscA.frequency.value = 440 //440Hz

            //Create an LFO
            const lfo = context.createOscillator()
            lfo.type = 'sine'
            lfo.frequency.value = 1 //1Hz - 1 sweep / sec

            //Create a gain node
            const gain = new GainNode(context)
            gain.value = 1

            //Connect the LFO to the gain node's gain value
            lfo.connect(gain.gain)

            //Connect the A oscillator to the gain node
            //and connect the gain node to the output
            oscA.connect(gain).connect(context.destination)

            //Start the LFO
            lfo.start()

            //Start the A oscillator
            oscA.start(context.currentTime)

            //Stop after 3 sweeps
            oscA.stop(context.currentTime + 3)

            //Be creative
            weehoo()

        }

        console.log("current key " + event.key)

        if(event.key === 'Enter'){ //Enter
            currentCode.every((val,index)=>val === expectedCode[index]) && currentCode.length >= 9 ?
                win() : ()=>{}
            currentCode = []
        }else{
            currentCode.push(event.keyCode)
        }
    });

    const weehoo = () => {
        const john = document.createElement('img')
        john.style.background = 'white';
        john.src="/img/weehoo.png";
        john.style.width = '100%';
        john.style.height = '100%';
        john.style.position = 'absolute'
        john.style.left = '0px';
        john.style.top = '0px';
        document.body.append(john);
        setTimeout(()=>{
            john.remove();
        },5000)
    }
})()