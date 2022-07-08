async function handleRequest(request) {
    let response = SERVICE_NAME;
    if (request.method == "POST") {
        const header = Object.fromEntries(request.headers);
        if (header.hasOwnProperty("access-token")) {
            if(header["access-token"] === ACCESS_TOKEN) {
                let authentication = "";
                let destination = "";
                try {
                    const body = await request.json();
                    if(body.authentication === undefined 
                    || body.destination === undefined) {
                        throw "Missing Parameters";
                    }
                    authentication = body.authentication;
                    destination = body.destination;
                } catch(error) {
                    return new Response("Missing Body", {status: 500});
                }

                // Layout //

                let url = DESTINATION_URL + "/auth?token=" + authentication;
                let layout =`
                  <HTML>
                    <h3>Welcome...</h3>
                    <p>Use the magic link below to launch your RPS dashboard.</p>
                    <p>
                      <b>
                        <a href="${url}" style="text-decoration: none;">
                            Launch Dashboard
                        </a>
                      </b>
                    </p>
                  </HTML>
                `;

                // MailChannels Sender Start //

                let send = new Request(MAILCHANNELS_API, {
                    method: "POST",
                    headers: {
                        "content-type": "application/json",
                    },
                    body: JSON.stringify({
                        personalizations: [
                            {
                                to: [
                                    {
                                        email: destination,
                                    },
                                ],
                                dkim_domain: DKIM_DOMAIN, 
                                dkim_selector: "mailchannels",
                                dkim_private_key: DKIM_PRIVATE_KEY,
                            },
                        ],
                        from: {
                            email: FROM_EMAIL,
                            name: FROM_NAME,
                        },
                        subject: SUBJECT_EMAIL,
                        content: [
                            {
                                type: "text/html",
                                value: layout,
                            },
                        ],
                    }),
                });

                const resp = await fetch(send);
                
                // MailChannels Sender End //

                if (resp.status === 202) {
                    return new Response("Success");
                } else {
                    const data = await resp.text(); console.log(data);
                    return new Response("Failure", {status: 500});
                }
            }
        }
        return new Response("Missing Header", {status: 500});
    }
    return new Response(response);
}

addEventListener("fetch", event => {
    event.respondWith(handleRequest(event.request))
})