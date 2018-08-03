let ws = null;

let attempts = 0;
let wasConnected = false;
let preferFallback = false;

let connectionId = null;

let messageCount = 0;

const REQUEST_TIMEOUT = 30000;

function createWebSocket(cid, resolve, reject)
{
    const url = "ws://" + location.hostname + ":8080/skat-ws?cid="+ cid;
    ws = new WebSocket(url);

    ws.onopen = function () {
        console.log("ws.onopen");

        wasConnected = true;
        attempts = 0;
        //start = new Date().getTime();
        resolve(cid);
    };
    ws.onclose = function () {
        console.log("ws.onclose");
        if (!wasConnected || attempts > 2)
        {
            preferFallback = true;
            ws = createWebSocket(cid);
        }
        else
        {
            setTimeout(function () {
                ws = createWebSocket(cid);
            }, 1000);
        }
    };
    ws.onerror = function (err) {
        //console.log("ws.onerror");
        ws.onclose();
        reject(err);
    };
    ws.onmessage = ev => {
        console.log("ws.onMessage", ev);
        const data = JSON.parse(ev.data);

        //console.debug("RECEIVE: %o", data);

        const array = handlers[data.type];
        if (!array)
        {
            console.warn("unhandled message: ", data);
        }
        else
        {
            console.log("handlers", array);
            try
            {
                array.forEach(handler => handler(data));
            }
            catch (ex)
            {
                console.error("Error in Hub event handler", ex);
            }
        }
    };

    return ws;
}

function prepare(type, payload)
{
    const messageId = ++messageCount;

    return {
        connectionId,
        messageId,
        type,
        payload
    };
}

const handlers = {};

const promises = {};

function lookupPromiseArray(key)
{
    if (!key)
    {
        return null;
    }

    const promiseArray = promises[key];
    if (promiseArray)
    {
        window.clearTimeout(promiseArray[2]);
        delete promises[key];
    }
    return promiseArray;
}

const Hub = {
    register:
        function (type, fn) {
            //console.log("Hub.register(", type, fn, ")");

            let array = handlers[type];
            if (!array)
            {
                handlers[type] = array = [];
            }

            array.push(fn);
        },
    send:
    // Send message to server over socket.
        function (type, payload, noLog = false) {

            if (!noLog)
            {
                //console.log("SEND", type, payload)
            }

            const message = prepare(type, payload);

            //console.debug("send: ", message);

            const json = JSON.stringify(message);
            ws.send(json);

            return message.messageId;
        },
    request:
    // Send message to server over socket.
        function (type, payload) {
            return new Promise((resolve, reject) => {

                //console.log("REQUEST", type, payload);

                const messageId = this.send(type, payload, true);
                const timerId = window.setTimeout(() => {

                    // promise still active?
                    const promiseArray = promises[messageId];
                    if (promiseArray)
                    {
                        promiseArray[1](new Error("Timeout"));
                    }

                    delete promises[messageId];

                }, REQUEST_TIMEOUT);

                promises[messageId] = [resolve, reject, timerId];
            });
        },
    init:
        function (cid) {
            connectionId = cid;

            return new Promise(function (resolve, reject) {

                ws = createWebSocket(cid, resolve, reject);

                return cid;
            });
        }
};

Hub.register("ERROR", function (data) {

    console.error("ERROR: %s", data.message);
});

Hub.register("RESPONSE", function (reply) {
    //console.log("RESPONSE", reply);

    const promiseArray = lookupPromiseArray(reply.responseTo);
    const { payload } = reply;
    if (promiseArray)
    {
        const [ resolve, reject ] = promiseArray;

        if (!reply.error)
        {
            //console.log("REPLY", payload);
            resolve(payload);
        }
        else
        {
            reject(new Error(JSON.stringify(payload)));
        }
    }
    else
    {
        console.warn("received reply without corresponding source message: %o", reply);
    }
});

export default Hub;
