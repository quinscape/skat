import loadSVG from "./loadSVG";


const SYMBOL_RE =/^symbol:(.*?)$/;
const CONTAINER_ID = "svg-loader-container";

function transformChain(group)
{
    let transform = "";

    while (group && group.tagName !== "svg")
    {
        const t = group.getAttribute("transform");
        if (t)
        {
            transform += " " + t;
        }
        group = group.parentNode;
    }

    return transform;
}

export default function(uri) {
    return loadSVG(uri).then(doc => {

        const div = document.createElement("div");
        div.id = CONTAINER_ID;
        div.className = "preload";

        const svgContainer = doc.documentElement;
        div.appendChild(svgContainer);
        document.body.appendChild(div);

        const symbols = {};

        Array.prototype.map.call(div.getElementsByTagName("g"), group => {

            const id = group.getAttribute("id");

            const m = SYMBOL_RE.exec(id);


            if (m)
            {
                const containerRect = svgContainer.getBoundingClientRect();

                const aabb = group.getBoundingClientRect();

                const name = m[1];
                symbols[name] = {
                    snippet: { __html : group.innerHTML },
                    aabb: {
                        x: aabb.x - containerRect.x,
                        y: aabb.y - containerRect.y,
                        width: aabb.width,
                        height: aabb.height
                    },
                    transform: transformChain(group) || ""
                };
            }
        });

        //document.body.removeChild(div);

        return symbols;
    })
    .catch(err => console.error("ERROR", err));
}
